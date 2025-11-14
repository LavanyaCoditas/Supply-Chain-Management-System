package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.ToolRequestService;
import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.constants.ToolIssuanceStatus;
import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.GetToolRequestDto;
import com.project.supply.chain.management.dto.ToolRequestDto;
import com.project.supply.chain.management.entity.*;
import com.project.supply.chain.management.specifications.ToolRequestSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
    @RequiredArgsConstructor
    @Transactional
    public class ToolRequestServiceImpl implements ToolRequestService {

    private final UserRepository userRepository;
    private final ToolsRepository toolRepository;
    private final ToolRequestRepository toolRequestRepository;
    private final ToolStockRepository toolStockRepository;
    private final ToolIssuanceRepository toolIssuanceRepository;
    private final UserFactoryMappingRepository userFactoryMappingRepository;

    // -------------------------- WORKER CREATES REQUEST --------------------------
    @Override
    @Transactional
    public ApiResponseDto<String> requestTool(ToolRequestDto dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User worker = userRepository.findByEmail(email);

        if (worker == null) throw new RuntimeException("User not found");
        if (worker.getRole() != Role.WORKER)
            throw new RuntimeException("Only workers can request tools");

        List<Long> toolIds = dto.getToolIds();
        List<Integer> quantities = dto.getQuantities();

        if (toolIds == null || toolIds.isEmpty())
            throw new RuntimeException("Tool list cannot be empty");

        if (quantities == null || quantities.size() != toolIds.size())
            throw new RuntimeException("Tool IDs and quantities must match");
        List<ToolRequestItem> expensiveItems = new ArrayList<>();
        List<ToolRequestItem> normalItems = new ArrayList<>();


        for (int i = 0; i < toolIds.size(); i++) {

            Long toolId = toolIds.get(i);
            Integer qty = quantities.get(i);

            Tool tool = toolRepository.findById(toolId)
                    .orElseThrow(() -> new RuntimeException("Tool not found with ID: " + toolId));

            if (qty == null || qty <= 0)
                throw new RuntimeException("Quantity must be greater than 0");

            ToolRequestItem item = new ToolRequestItem();
            item.setTool(tool);
            item.setQuantity(qty);

            if (tool.getIsExpensive().name().equalsIgnoreCase("YES"))
                expensiveItems.add(item);
            else
                normalItems.add(item);
        }

        // Create request for expensive tools
        if (!expensiveItems.isEmpty())
            createToolRequest(worker, expensiveItems);

        // Create request for normal tools
        if (!normalItems.isEmpty())
            createToolRequest(worker, normalItems);

        return new ApiResponseDto<>(true, "Tool request(s) submitted successfully", null);
    }

    private void createToolRequest(User worker, List<ToolRequestItem> items) {

        ToolRequest request = new ToolRequest();
        request.setWorker(worker);
        request.setStatus(ToolOrProductRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        items.forEach(i -> i.setToolRequest(request));
        request.setToolItems(items);

        toolRequestRepository.save(request);
    }


    // ------------------------- APPROVER HANDLES REQUEST -------------------------
    @Override
    @Transactional
    public ApiResponseDto<String> handleToolRequest(Long requestId, boolean approve, String reason) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User approver = userRepository.findByEmail(email);

        if (approver == null)
            throw new RuntimeException("User not found");

        ToolRequest request = toolRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Tool request not found"));

        List<ToolRequestItem> items = request.getToolItems();

        boolean hasExpensiveTool = items.stream()
                .anyMatch(i -> i.getTool().getIsExpensive().name().equalsIgnoreCase("YES"));

        // 🔐 Role validation
        if (hasExpensiveTool && approver.getRole() != Role.PLANT_HEAD)
            throw new RuntimeException("Only Plant Head can approve expensive tool requests");

        if (!hasExpensiveTool && approver.getRole() != Role.CHIEF_SUPERVISOR)
            throw new RuntimeException("Only Chief Supervisor can approve normal tool requests");

        // ❌ Reject
        if (!approve) {
            request.setStatus(ToolOrProductRequestStatus.REJECTED);
            request.setRejectionReason(reason);
            request.setApprovedBy(approver);
            request.setUpdatedAt(LocalDateTime.now());
            toolRequestRepository.save(request);

            return new ApiResponseDto<>(true, "Tool request rejected successfully", null);
        }

        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(request.getWorker())
                .orElseThrow(() -> new RuntimeException("Worker is not mapped to any factory"));

        Factory factory = mapping.getFactory();


        for (ToolRequestItem item : items) {

            Tool tool = item.getTool();

            ToolStock stock = toolStockRepository.findByToolAndFactory(tool, factory)
                    .orElseThrow(() -> new RuntimeException("Tool " + tool.getName() + " not found in factory stock"));

            if (stock.getAvailableQuantity() < item.getQuantity())
                throw new RuntimeException("Insufficient stock for tool: " + tool.getName());

            // Update stock
            stock.setAvailableQuantity(stock.getAvailableQuantity() - item.getQuantity());
            stock.setIssuedQuantity(stock.getIssuedQuantity() + item.getQuantity());
            stock.setLastUpdatedAt(LocalDateTime.now());

            toolStockRepository.save(stock);

            // Issuance record
            ToolIssuance issuance = new ToolIssuance();
            issuance.setRequest(request);
            issuance.setTool(tool);
            issuance.setStatus(ToolIssuanceStatus.ISSUED);
            toolIssuanceRepository.save(issuance);
        }

        // Update request
        request.setStatus(ToolOrProductRequestStatus.APPROVED);
        request.setApprovedBy(approver);
        request.setUpdatedAt(LocalDateTime.now());

        toolRequestRepository.save(request);

        return new ApiResponseDto<>(true, "Tool request approved successfully", null);
    }

}
