package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.ProductRestockRequestService;
import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.CreateRestockRequestDto;
import com.project.supply.chain.management.dto.ProductRestockRequestDto;
import com.project.supply.chain.management.dto.UpdateProductStockDto;
import com.project.supply.chain.management.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductRestockRequestServiceImpl implements ProductRestockRequestService {

    private final FactoryRepository factoryRepository;
    private final ProductRepository productRepository;
    private final CentralOfficeProductRequestRepository centralOfficeProductRequestRepository;
    private final UserRepository userRepository;
    private final FactoryProductionRepository factoryProductionRepository;
    private final ToolRequestRepository toolRequestRepository;

    @Override
    public ApiResponseDto<ProductRestockRequestDto> createRestockRequest(CreateRestockRequestDto dto) {
        try {
            // Get logged-in user
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(email);

            if (currentUser == null)
                throw new RuntimeException("User not found");
            if (currentUser.getRole() != Role.CENTRAL_OFFICE)
                throw new RuntimeException("Only Central Officer can create restock requests");

            // ✅ Validate Factory
            Factory factory = factoryRepository.findById(dto.getFactoryId())
                    .orElseThrow(() -> new RuntimeException("Factory not found with ID: " + dto.getFactoryId()));


            // ✅ Validate Product
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found for ID: " + dto.getProductId()));

            // ✅ Validate Quantity
            if (dto.getQtyRequested() == null || dto.getQtyRequested() <= 0)
                throw new RuntimeException("Quantity must be greater than 0");

            // ✅ Create Entity
            CentralOfficeProductRequest restockRequest = new CentralOfficeProductRequest();
            restockRequest.setFactory(factory);
            restockRequest.setProduct(product);
            restockRequest.setQtyRequested(dto.getQtyRequested());
            restockRequest.setStatus(ToolOrProductRequestStatus.PENDING);
            restockRequest.setRequestedAt(LocalDateTime.now());
            restockRequest.setRequestedByUser(currentUser);

            // Save request
            CentralOfficeProductRequest saved = centralOfficeProductRequestRepository.save(restockRequest);

            // ✅ Convert to DTO
            ProductRestockRequestDto responseDto = new ProductRestockRequestDto(
                    saved.getId(),
                    saved.getFactory().getId(),
                    saved.getFactory().getName(),
                    saved.getProduct().getId(),
                    saved.getProduct().getName(),
                    saved.getQtyRequested(),
                    saved.getStatus(),
                    saved.getRequestedAt(),
                    saved.getRequestedByUser().getId(),
                    saved.getRequestedByUser().getUsername(),
                    saved.getFactory().getPlanthead() != null ? saved.getFactory().getPlanthead().getId() : null,
                    saved.getFactory().getPlanthead() != null ? saved.getFactory().getPlanthead().getUsername() : null,
                    saved.getCompletedAt()
            );

            return new ApiResponseDto<>(true, "Restock request created successfully", responseDto);

        } catch (Exception e) {
            return new ApiResponseDto<>(false, "Failed to create restock request: " + e.getMessage(), null);
        }
    }


//    @Override
//    public ApiResponseDto<String> handleToolRequest(Long requestId, boolean approve, String reason) {
//
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User approver = userRepository.findByEmail(email);
//
//        if (approver == null) throw new RuntimeException("User not found");
//
//        ToolRequest request = toolRequestRepository.findById(requestId)
//                .orElseThrow(() -> new RuntimeException("Request not found"));
//
//        // Status check
//        if (request.getStatus() != ToolOrProductRequestStatus.PENDING)
//            throw new RuntimeException("Only pending requests can be processed");
//
//        boolean hasExpensiveTool = request.getToolItems()
//                .stream()
//                .anyMatch(i -> i.getTool().getIsExpensive() == Expensive.YES);
//
//        // Role-based access control
//        if (hasExpensiveTool && approver.getRole() != Role.PLANT_HEAD)
//            throw new RuntimeException("Only Plant Head can approve expensive tool requests");
//
//        if (!hasExpensiveTool && approver.getRole() != Role.CHIEF_SUPERVISOR)
//            throw new RuntimeException("Only Chief Supervisor can approve normal requests");
//
//        // REJECT FLOW
//        if (!approve) {
//            if (reason == null || reason.trim().isEmpty())
//                throw new RuntimeException("Rejection reason is required");
//
//            request.setStatus(ToolOrProductRequestStatus.REJECTED);
//            request.setRejectionReason(reason);
//            toolRequestRepository.save(request);
//
//            return new ApiResponseDto<>(true, "Request rejected successfully", null);
//        }
//
//        // APPROVAL FLOW — CHECK STOCK FIRST
//        for (ToolRequestItem item : request.getToolItems()) {
//
//            Tool tool = item.getTool();
//            if (tool.getQty() < item.getQuantity()) {
//                throw new RuntimeException(
//                        "Insufficient stock for tool: " + tool.getName()
//                );
//            }
//        }
//
//        // Deduct stock
//        for (ToolRequestItem item : request.getToolItems()) {
//            Tool tool = item.getTool();
//            tool.setQty(tool.getQty() - item.getQuantity());
//            toolRepository.save(tool);
//        }
//
//        // Approve
//        request.setStatus(ToolOrProductRequestStatus.APPROVED);
//        toolRequestRepository.save(request);
//
//        return new ApiResponseDto<>(true, "Request approved successfully", null);
//    }

//
//    @Override
//    public ApiResponseDto<ProductRestockRequestDto> completeRestockRequest(Long requestId) {
//        try {
//            // 🔐 Get logged-in user
//            String email = SecurityContextHolder.getContext().getAuthentication().getName();
//            User currentUser = userRepository.findByEmail(email);
//
//            if (currentUser == null)
//                throw new RuntimeException("User not found");
//
//            // ✅ Only PLANT_HEAD can complete restock requests
//            if (currentUser.getRole() != Role.PLANT_HEAD)
//                throw new RuntimeException("Only Plant Head can complete restock requests");
//
//            // 🧾 Fetch request
//            CentralOfficeProductRequest restockRequest = centralOfficeProductRequestRepository.findById(requestId)
//                    .orElseThrow(() -> new RuntimeException("Restock request not found"));
//
//            // ✅ Check status
//            if (restockRequest.getStatus() != ToolOrProductRequestStatus.PENDING)
//                throw new RuntimeException("Only pending requests can be completed");
//
//            // ✅ Check if the logged-in Plant Head is assigned to this factory
//            Factory factory = restockRequest.getFactory();
//            if (factory.getPlanthead() == null ||
//                    !factory.getPlanthead().getId().equals(currentUser.getId())) {
//                throw new RuntimeException("You don't have access to complete this factory's request");
//            }
//
//            // ✅ Update status
//            restockRequest.setStatus(ToolOrProductRequestStatus.COMPLETED);
//            restockRequest.setCompletedAt(LocalDateTime.now());
//
//            CentralOfficeProductRequest updated = centralOfficeProductRequestRepository.save(restockRequest);
//
//            // TODO: Optionally update factory production or inventory here if needed
//            // updateProductionAndInventory(factory, restockRequest.getProduct(), restockRequest.getQtyRequested(), currentUser);
//
//            // ✅ Map to DTO
//            ProductRestockRequestDto dto = new ProductRestockRequestDto(
//                    updated.getId(),
//                    updated.getFactory().getId(),
//                    updated.getFactory().getName(),
//                    updated.getProduct().getId(),
//                    updated.getProduct().getName(),
//                    updated.getQtyRequested(),
//                    updated.getStatus(),
//                    updated.getRequestedAt(),
//                    updated.getRequestedByUser().getId(),
//                    updated.getRequestedByUser().getUsername(),
//                    currentUser.getId(),
//                    currentUser.getUsername(),
//                    updated.getCompletedAt()
//            );
//
//            return new ApiResponseDto<>(true, "Restock request marked as completed successfully", dto);
//
//        } catch (Exception e) {
//            return new ApiResponseDto<>(false, "Failed to complete restock request: " + e.getMessage(), null);
//        }
//    }


//    @Override
//    public ApiResponseDto<String> updateStockDirectly(UpdateProductStockDto stockDto) {
//        try {
//            // 🔐 Get current logged-in user
//            String email = SecurityContextHolder.getContext().getAuthentication().getName();
//            User currentUser = userRepository.findByEmail(email);
//
//            if (currentUser == null)
//                throw new RuntimeException("User not found");
//
//            if (currentUser.getRole() != Role.PLANT_HEAD)
//                throw new RuntimeException("Only Plant Heads can update stock directly");
//
//            // ✅ Find factory assigned to this plant head
//            Factory factory = factoryRepository.findByPlanthead(currentUser)
//                    .orElseThrow(() -> new RuntimeException("No factory assigned to this Plant Head"));
//
//            // ✅ Validate product
//            Product product = productRepository.findById(stockDto.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Product not found for ID: " + stockDto.getProductId()));
//
//            // ✅ Validate quantity
//            if (stockDto.getQuantity() == null || stockDto.getQuantity() <= 0)
//                throw new RuntimeException("Quantity must be greater than 0");
//
//            // ✅ Update or insert record in Factory_Production
//           FactoryProduction production = factoryProductionRepository
//                    .findByFactoryAndProduct(factory, product);
//
//
//           production.setProduct(product);
//           production.setFactory(factory);
//            production.setProducedQty(production.getProducedQty() + stockDto.getQuantity());
//            production.setProductionDate(LocalDateTime.now());
//
//            factoryProductionRepository.save(production);
//
//            return new ApiResponseDto<>(true,
//                    "Stock updated successfully for factory: " + factory.getName(),
//                    null);
//
//        } catch (Exception e) {
//            return new ApiResponseDto<>(false,
//                    "Failed to update stock: " + e.getMessage(),
//                    null);
//        }
//    }


}

