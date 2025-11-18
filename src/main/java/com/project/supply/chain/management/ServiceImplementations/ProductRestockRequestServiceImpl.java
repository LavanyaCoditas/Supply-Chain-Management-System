package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.ProductRestockRequestService;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.*;
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import com.project.supply.chain.management.exceptions.UnauthorizedAccessException;
import com.project.supply.chain.management.exceptions.UserNotFoundException;
import com.project.supply.chain.management.specifications.CentralOfficeInventorySpecifications;
import com.project.supply.chain.management.util.ApplicationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRestockRequestServiceImpl implements ProductRestockRequestService {

    private final FactoryRepository factoryRepository;
    private final ProductRepository productRepository;
    private final CentralOfficeProductRequestRepository centralOfficeProductRequestRepository;
    private final FactoryProductionRepository factoryProductionRepository;
    private final FactoryInventoryStockRepository factoriesInventoryStockRepository;
    private final CentralOfficeInventoryRepository centralOfficeInventoryRepository;
    private final ApplicationUtils applicationUtils;
    private final UserFactoryMappingRepository userFactoryMappingRepository;


    @Override
    @Transactional
    public ApiResponseDto<ProductRestockRequestDto> createRestockRequest(CreateRestockRequestDto requestDto) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser.getRole() != Role.CENTRAL_OFFICE) {
                throw new UnauthorizedAccessException("Only central officers can create restock requests");
            }

            Factory factory = factoryRepository.findById(requestDto.getFactoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Factory not found with ID: " + requestDto.getFactoryId()));

            Product product = productRepository.findById(requestDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + requestDto.getProductId()));

            if (requestDto.getQtyRequested() == null || requestDto.getQtyRequested() <= 0) {
                throw  new IllegalArgumentException( "Quantity must be greater than 0");
            }

            // Get current factory stock
            Integer currentFactoryStock = getCurrentStock(factory, product);

            CentralOfficeProductRequest request = new CentralOfficeProductRequest();
            request.setFactory(factory);
            request.setProduct(product);
            request.setQtyRequested(requestDto.getQtyRequested());
            request.setStatus(ToolOrProductRequestStatus.PENDING);
            request.setRequestedAt(LocalDateTime.now());
            request.setRequestedByUser(currentUser);

            CentralOfficeProductRequest savedRequest = centralOfficeProductRequestRepository.save(request);

            // Convert to response DTO
            ProductRestockRequestDto responseDto = convertToRestockRequestDto(savedRequest);
            responseDto.setCurrentFactoryStock(currentFactoryStock);

            return new ApiResponseDto<>(true, "Restock request created successfully", responseDto);

        } catch (UnauthorizedAccessException | ResourceNotFoundException e) {
            log.warn("Business rule violation: {}", e.getMessage());
            return new ApiResponseDto<>(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Failed to create restock request", e);
            return new ApiResponseDto<>(false, "Failed to create restock request: " + e.getMessage(), null);
        }
    }
    @Override
    public ApiResponseDto<Page<CentralOfficeInventoryDto>> getCentralOfficeInventory(
            Long productId, String productName, Long minQuantity, Long maxQuantity, BaseRequestDto requestDto) {
        try {
            // Build specification with filters
            Specification<CentralOfficeInventory> spec = CentralOfficeInventorySpecifications.withFilters(
                    productId, productName, minQuantity, maxQuantity
            );

            int page = requestDto.getPage() == null ? 0 : requestDto.getPage();
            int size = requestDto.getSize() == null ? 20 : requestDto.getSize();
            Pageable pageable = PageRequest.of(page, size);

            // Execute query and map to DTO
            Page<CentralOfficeInventoryDto> resultPage = centralOfficeInventoryRepository.findAll(spec, pageable)
                    .map(this::convertToInventoryDto);

            String message = "Central office inventory retrieved successfully";
            return new ApiResponseDto<>(true, message, resultPage);
        } catch (Exception e) {
            log.error("Failed to get central office inventory", e);
            return new ApiResponseDto<>(false, "Failed to get central office inventory: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponseDto<Page<ProductRestockRequestDto>> getMyRestockRequests(
            ToolOrProductRequestStatus status, BaseRequestDto requestDto) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser.getRole() != Role.CENTRAL_OFFICE) {
                throw new UnauthorizedAccessException("Only central officers can view their requests");
            }

            Pageable pageable = createPageable(requestDto);
            Page<CentralOfficeProductRequest> restockPage = (status != null)
                    ? centralOfficeProductRequestRepository.findByRequestedByUserIdAndStatus(currentUser.getId(), status, pageable)
                    : centralOfficeProductRequestRepository.findByRequestedByUser(currentUser, pageable);
//Page<CentralOfficeProductRequest> findByRequestedByUserIdAndStatus(Long chiefOfficerId, ToolOrProductRequestStatus status, Pageable pageable);
//    Page<CentralOfficeProductRequest> findByStatus(ToolOrProductRequestStatus s
            Page<ProductRestockRequestDto> resultPage = restockPage.map(this::convertToRestockRequestDto);

            String message = status != null
                    ? "Your " + status + " restock requests retrieved successfully"
                    : "Your restock requests retrieved successfully";

            return new ApiResponseDto<>(true, message, resultPage);

        } catch (UnauthorizedAccessException e) {
            log.warn("Unauthorized access attempt: {}", e.getMessage());
            return new ApiResponseDto<>(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Failed to retrieve your restock requests", e);
            return new ApiResponseDto<>(false, "Failed to retrieve your restock requests: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponseDto<String> updateStockDirectly(UpdateProductStockDto stockDto) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser.getRole() != Role.PLANT_HEAD) {
                throw new UnauthorizedAccessException("Only factory PlantHead can update stock");
            }

            Long factoryId = getCurrentUserFactoryId(currentUser);
            if (factoryId == null) {
                return new ApiResponseDto<>(false, "Manager is not assigned to any factory", null);
            }

            Factory factory = factoryRepository.findById(factoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Factory not found with ID: " + factoryId));

            Product product = productRepository.findById(stockDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + stockDto.getProductId()));

            if (stockDto.getQuantity() == null || stockDto.getQuantity() <= 0) {
                return new ApiResponseDto<>(false, "Quantity must be greater than 0", null);
            }

            // Add to factory inventory and track production
            addToFactoryInventory(factory, product, stockDto.getQuantity(), currentUser);
            trackFactoryProduction(factory, product, stockDto.getQuantity());

            return new ApiResponseDto<>(true,
                    stockDto.getQuantity() + " units added to " + factory.getName() + " inventory", null);

        } catch (UnauthorizedAccessException | ResourceNotFoundException e) {
            log.warn("Business rule violation: {}", e.getMessage());
            return new ApiResponseDto<>(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Failed to update stock", e);
            return new ApiResponseDto<>(false, "Failed to update stock: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponseDto<ProductRestockRequestDto> completeRestockRequest(Long requestId) {
        try {
            User currentUser = getCurrentUser();
            if (!isManagerOrOwner(currentUser)) {
                throw new UnauthorizedAccessException("Only managers or owners can complete restock requests");
            }

            CentralOfficeProductRequest request = centralOfficeProductRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Restock request not found with ID: " + requestId));

            if (!hasAccessToFactory(currentUser, request.getFactory().getId())) {
                throw new UnauthorizedAccessException("You don't have access to complete requests for this factory");
            }

            if (request.getStatus() != ToolOrProductRequestStatus.PENDING) {
                return new ApiResponseDto<>(false, "Only pending requests can be completed", null);
            }

            // Check if factory has enough stock to fulfill the request
            if (!hasSufficientStock(request.getFactory(), request.getProduct(), request.getQtyRequested())) {
                return new ApiResponseDto<>(false, "Insufficient stock. Factory only has " +
                        getCurrentStock(request.getFactory(), request.getProduct()) +
                        " units of " + request.getProduct().getName(), null);
            }

            // Get current factory stock before deduction
            Integer currentFactoryStock = getCurrentStock(request.getFactory(), request.getProduct());

            // Deduct from factory inventory (transfer stock out to central office)
            deductFromFactoryInventory(request.getFactory(), request.getProduct(), request.getQtyRequested());

            // Add to central office inventory
            addToCentralOfficeInventory(request.getProduct(), request.getQtyRequested());

            // Update request status to COMPLETED
            request.setStatus(ToolOrProductRequestStatus.COMPLETED);
            request.setCompletedAt(LocalDateTime.now());
            CentralOfficeProductRequest updatedRequest = centralOfficeProductRequestRepository.save(request);

            // Return response DTO
            ProductRestockRequestDto responseDto = convertToRestockRequestDto(updatedRequest);
            responseDto.setCurrentFactoryStock(currentFactoryStock);

            return new ApiResponseDto<>(true,
                    "Restock request completed successfully. " + request.getQtyRequested() +
                            " units transferred from " + request.getFactory().getName() + " to central office",
                    responseDto);

        } catch (UnauthorizedAccessException | ResourceNotFoundException e) {
            log.warn("Business rule violation: {}", e.getMessage());
            return new ApiResponseDto<>(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Failed to complete restock request", e);
            return new ApiResponseDto<>(false, "Failed to complete restock request: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponseDto<Page<ProductRestockRequestDto>> getMyFactoryRestockRequests(
            ToolOrProductRequestStatus status, BaseRequestDto requestDto) {
        try {
            User currentUser = getCurrentUser();
            if (!isManagerOrOwner(currentUser)) {
                throw new UnauthorizedAccessException("Only managers or owners can view factory requests");
            }

            Long factoryId = getCurrentUserFactoryId(currentUser);
            if (factoryId == null) {
                return new ApiResponseDto<>(false, "No factory assigned to your account", null);
            }

            Pageable pageable = createPageable(requestDto);
            Page<CentralOfficeProductRequest> restockPage = (status != null)
                    ? centralOfficeProductRequestRepository.findByFactoryIdAndStatus(factoryId, status, pageable)
                    : centralOfficeProductRequestRepository.findByFactoryId(factoryId, pageable);

            Page<ProductRestockRequestDto> resultPage = restockPage.map(this::convertToRestockRequestDto);

            String message = status != null
                    ? status + " restock requests for your factory retrieved successfully"
                    : "All restock requests for your factory retrieved successfully";

            return new ApiResponseDto<>(true, message, resultPage);

        } catch (UnauthorizedAccessException e) {
            log.warn("Unauthorized access attempt: {}", e.getMessage());
            return new ApiResponseDto<>(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Failed to retrieve factory restock requests", e);
            return new ApiResponseDto<>(false, "Failed to retrieve factory restock requests: " + e.getMessage(), null);
        }
    }

    // HELPER METHODS USING APPLICATION UTILS
    private User getCurrentUser() {
        String email = applicationUtils.getLoggedInUserEmail();
        User user = applicationUtils.getUser(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    private Long getCurrentUserFactoryId(User currentUser) {
        Optional<UserFactoryMapping> mappings = userFactoryMappingRepository.findByUser(currentUser);
        if (mappings != null && !mappings.isEmpty()) {
            return mappings.get().getFactory().getId();
        }
        return null;}

    private boolean isManagerOrOwner(User user) {
        return user.getRole() == Role.PLANT_HEAD || user.getRole() == Role.OWNER;
    }

    private boolean hasAccessToFactory(User user, Long factoryId) {
        // Implement your factory access logic here
        // For managers, check if they are assigned to this factory
        if (user.getRole() == Role.PLANT_HEAD) {
            return factoryId.equals(getCurrentUserFactoryId(user));
        }
        // Owners have access to all factories
        return user.getRole() == Role.OWNER;
    }

    private Long addToCentralOfficeInventory(Product product, Integer quantity) {
        CentralOfficeInventory centralInventory = centralOfficeInventoryRepository.findByProduct(product)
                .orElse(new CentralOfficeInventory(product, 0L));

        centralInventory.addQuantity(quantity.longValue());
        CentralOfficeInventory savedInventory = centralOfficeInventoryRepository.save(centralInventory);

        return savedInventory.getQuantity();
    }

    private boolean hasSufficientStock(Factory factory, Product product, Integer requestedQty) {
        Integer currentStock = getCurrentStock(factory, product);
        return currentStock >= requestedQty;
    }

    private Integer getCurrentStock(Factory factory, Product product) {
        return factoriesInventoryStockRepository.findByFactoryAndProduct(factory, product)
                .map(FactoriesInventoryStock::getQty)
                .orElse(0);
    }

    private void addToFactoryInventory(Factory factory, Product product, Integer quantity, User addedBy) {
        FactoriesInventoryStock inventory = factoriesInventoryStockRepository.findByFactoryAndProduct(factory, product)
                .orElse(new FactoriesInventoryStock());

        if (inventory.getStockEntryId() == null) {
            inventory.setFactory(factory);
            inventory.setProduct(product);
            inventory.setQty(quantity);
            inventory.setAddedBy(addedBy);
        } else {
            inventory.setQty(inventory.getQty() + quantity);
        }
        factoriesInventoryStockRepository.save(inventory);
    }

    private void deductFromFactoryInventory(Factory factory, Product product, Integer quantity) {
        FactoriesInventoryStock inventory = factoriesInventoryStockRepository.findByFactoryAndProduct(factory, product)
                .orElseThrow(() -> new ResourceNotFoundException("Factory inventory not found for product: " + product.getName()));

        if (inventory.getQty() < quantity) {
            throw new RuntimeException("Insufficient stock in factory inventory");
        }

        inventory.setQty(inventory.getQty() - quantity);
        factoriesInventoryStockRepository.save(inventory);
    }

    private void trackFactoryProduction(Factory factory, Product product, Integer quantity) {
        FactoryProduction production = factoryProductionRepository.findByFactoryAndProduct(factory, product)
                .orElse(new FactoryProduction());

        if (production.getId() == null) {
            production.setFactory(factory);
            production.setProduct(product);
            production.setProducedQty(quantity);
            production.setProductionDate(LocalDateTime.now());
        } else {
            production.setProducedQty(production.getProducedQty() + quantity);
            production.setProductionDate(LocalDateTime.now());
        }
        factoryProductionRepository.save(production);
    }

    private ProductRestockRequestDto convertToRestockRequestDto(CentralOfficeProductRequest request) {
        ProductRestockRequestDto dto = new ProductRestockRequestDto();
        dto.setId(request.getId());
        dto.setFactoryId(request.getFactory().getId());
        dto.setFactoryName(request.getFactory().getName());
        dto.setProductId(request.getProduct().getId());
        dto.setProductName(request.getProduct().getName());
        dto.setQtyRequested(request.getQtyRequested());
        dto.setStatus(request.getStatus());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setCompletedAt(request.getCompletedAt());

        // Add user information
        if (request.getRequestedByUser() != null) {
            dto.setRequestedByUserId(request.getRequestedByUser().getId());
            dto.setRequestedByUserName(request.getRequestedByUser().getUsername());
        }

        // Add current factory stock
        Integer currentStock = getCurrentStock(request.getFactory(), request.getProduct());
        dto.setCurrentFactoryStock(currentStock);

        return dto;
    }

    private CentralOfficeInventoryDto convertToInventoryDto(CentralOfficeInventory inventory) {
        CentralOfficeInventoryDto dto = new CentralOfficeInventoryDto();

        if (inventory.getProduct() != null) {
            dto.setProductId(inventory.getProduct().getId());
            dto.setProductName(inventory.getProduct().getName());
        }

        dto.setQuantity(inventory.getQuantity());
        dto.setTotalReceived(inventory.getTotalReceived());
        return dto;
    }

    private Pageable createPageable(BaseRequestDto requestDto) {
        int page = requestDto.getPage() == null ? 0 : requestDto.getPage();
        int size = requestDto.getSize() == null ? 20 : requestDto.getSize();
        return PageRequest.of(page, size);
    }



    }

