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
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import com.project.supply.chain.management.exceptions.UnauthorizedAccessException;
import com.project.supply.chain.management.exceptions.UserNotFoundException;
import com.project.supply.chain.management.util.ApplicationUtils;
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
    private final ApplicationUtils appUtils;


    @Override
    public ApiResponseDto<ProductRestockRequestDto> createRestockRequest(CreateRestockRequestDto dto) {
        try {

            User currentUser=appUtils.getUser(appUtils.getLoggedInUserEmail());

            if (currentUser == null)
                throw new UserNotFoundException("User not found");
            if (currentUser.getRole() != Role.CENTRAL_OFFICE)
                throw new UnauthorizedAccessException("Only Central Officer can create restock requests");

            //  Validate Factory
            Factory factory = factoryRepository.findById(dto.getFactoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Factory not found with ID: " + dto.getFactoryId()));


            //  Validate Product
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + dto.getProductId()));

            //  Validate Quantity
            if (dto.getQtyRequested() == null || dto.getQtyRequested() <= 0)
                throw new IllegalArgumentException("Quantity must be greater than 0");


            CentralOfficeProductRequest restockRequest = new CentralOfficeProductRequest();
            restockRequest.setFactory(factory);
            restockRequest.setProduct(product);
            restockRequest.setQtyRequested(dto.getQtyRequested());
            restockRequest.setStatus(ToolOrProductRequestStatus.PENDING);
            restockRequest.setRequestedAt(LocalDateTime.now());
            restockRequest.setRequestedByUser(currentUser);


            CentralOfficeProductRequest saved = centralOfficeProductRequestRepository.save(restockRequest);


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


    }

