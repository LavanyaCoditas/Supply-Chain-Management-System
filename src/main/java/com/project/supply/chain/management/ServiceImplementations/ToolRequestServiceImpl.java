package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.ToolRequestService;
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
            throw new RuntimeException("Tool IDs and quantities must have the same length");

        // 🧾 Create parent ToolRequest
        ToolRequest request = new ToolRequest();
        request.setWorker(worker);
        request.setStatus(ToolOrProductRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        List<ToolRequestItem> items = new ArrayList<>();

        for (int i = 0; i < toolIds.size(); i++) {
            Long toolId = toolIds.get(i);
            Integer qty = quantities.get(i);

            if (qty == null || qty <= 0)
                throw new RuntimeException("Quantity must be greater than 0 for tool ID: " + toolId);

            Tool tool = toolRepository.findById(toolId)
                    .orElseThrow(() -> new RuntimeException("Tool not found for ID: " + toolId));

            ToolRequestItem item = new ToolRequestItem();
            item.setToolRequest(request);
            item.setTool(tool);
            item.setQuantity(qty);

            items.add(item);
        }

        request.setToolItems(items);
        toolRequestRepository.save(request); // cascade = ALL will save items too

        String approver = items.stream()
                .map(i -> i.getTool().getIsExpensive().name().equalsIgnoreCase("YES") ? "Plant Head" : "Chief Supervisor")
                .distinct()
                .reduce((a, b) -> a.equals(b) ? a : "Plant Head or Chief Supervisor")
                .orElse("Approver");

        return new ApiResponseDto<>(true,
                "Tool request submitted successfully. Will be reviewed by " + approver,
                null);
    }




    // 🔧 Updated handle logic
    @Override
    @Transactional
    public ApiResponseDto<String> handleToolRequest(Long requestId, boolean approve, String reason) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User approver = userRepository.findByEmail(email);

        if (approver == null)
            throw new RuntimeException("User not found");

        ToolRequest request = toolRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Tool request not found"));

        // 🧾 Fetch all tools in the request
        List<ToolRequestItem> items = request.getToolItems();
        if (items == null || items.isEmpty())
            throw new RuntimeException("No tools found in this request");

        // 🧠 Determine if any item is expensive
        boolean hasExpensiveTool = items.stream()
                .anyMatch(i -> i.getTool().getIsExpensive().name().equalsIgnoreCase("YES"));

        // ✅ Role validation
        if (hasExpensiveTool && approver.getRole() != Role.PLANT_HEAD)
            throw new RuntimeException("Only Plant Head can approve requests with expensive tools");
        if (!hasExpensiveTool && approver.getRole() != Role.CHIEF_SUPERVISOR)
            throw new RuntimeException("Only Chief Supervisor can approve non-expensive tool requests");

        // 🏭 Find approver's factory
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(approver)
                .orElseThrow(() -> new RuntimeException("Approver not mapped to any factory"));
        Factory factory = mapping.getFactory();

        if (approve) {
            for (ToolRequestItem item : items) {
                Tool tool = item.getTool();

                // 🎯 Get factory stock for this tool
                ToolStock stock = toolStockRepository.findByToolAndFactory(tool, factory)
                        .orElseThrow(() -> new RuntimeException("Tool " + tool.getName() + " not found in factory stock"));

                if (stock.getAvailableQuantity() < item.getQuantity())
                    throw new RuntimeException("Insufficient stock for tool: " + tool.getName());

                // ✅ Update stock quantities
                stock.setAvailableQuantity(stock.getAvailableQuantity() - item.getQuantity());
                stock.setIssuedQuantity(stock.getIssuedQuantity() + item.getQuantity());
                stock.setLastUpdatedAt(LocalDateTime.now());
                toolStockRepository.save(stock);

                // 🧾 Create issuance record for each tool
                ToolIssuance issuance = new ToolIssuance();
                issuance.setRequest(request);
                issuance.setTool(tool);
                issuance.setStatus(ToolIssuanceStatus.ISSUED);
                toolIssuanceRepository.save(issuance);
            }

            // ✅ Update request metadata
            request.setStatus(ToolOrProductRequestStatus.APPROVED);
            request.setApprovedBy(approver);
            request.setUpdatedAt(LocalDateTime.now());

        } else {
            // ❌ Rejection case
            request.setStatus(ToolOrProductRequestStatus.REJECTED);
            request.setRejectionReason(reason);
            request.setApprovedBy(approver);
            request.setUpdatedAt(LocalDateTime.now());
        }

        toolRequestRepository.save(request);

        return new ApiResponseDto<>(
                true,
                approve ? "Tool request approved successfully" : "Tool request rejected successfully",
                null
        );
    }

    @Override
        public ApiResponseDto<List<GetToolRequestDto>> getPendingRequestsForApprover(
                String searchWorker,
                String searchTool,
                ToolOrProductRequestStatus status,
                int page,
                int size,
                String sortBy,
                String sortDir) {

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User approver = userRepository.findByEmail(email);

            if (approver == null) throw new RuntimeException("User not found");
            if (approver.getRole() != Role.PLANT_HEAD && approver.getRole() != Role.CHIEF_SUPERVISOR)
                throw new RuntimeException("Only Plant Head or Chief Supervisor can view requests");

            boolean isPlantHead = approver.getRole() == Role.PLANT_HEAD;

            // 🧩 Build Specification
            Specification<ToolRequest> spec = Specification.allOf(
                    ToolRequestSpecifications.hasStatus(status != null ? status : ToolOrProductRequestStatus.PENDING),
                    ToolRequestSpecifications.isExpensive(isPlantHead),
                    ToolRequestSpecifications.searchByToolName(searchTool),
                    ToolRequestSpecifications.searchByWorkerName(searchWorker)
            );

            // 📄 Pagination + Sorting
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ToolRequest> resultPage = toolRequestRepository.findAll(spec, pageable);

        List<GetToolRequestDto> dtos = resultPage.getContent().stream()
                .map(req -> {
                    List<String> toolNames = req.getToolItems().stream()
                            .map(item -> item.getTool().getName())
                            .toList();

                    List<Integer> quantities = req.getToolItems().stream()
                            .map(ToolRequestItem::getQuantity)
                            .toList();

                    return new GetToolRequestDto(
                            req.getId(),
                            toolNames,
                            req.getWorker().getUsername(),
                            quantities,
                            req.getStatus(),
                            req.getRejectionReason(),
                            req.getCreatedAt()
                    );
                })
                .toList();


        return new ApiResponseDto<>(true,
                    "Requests fetched successfully",
                    dtos);
        }

    }

