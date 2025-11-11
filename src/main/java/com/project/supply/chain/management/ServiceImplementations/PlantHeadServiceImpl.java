package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.PlantHeadService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.*;
import com.project.supply.chain.management.specifications.EmployeeSpecifications;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class PlantHeadServiceImpl implements PlantHeadService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    BayRepository bayRepository;
    @Autowired
    FactoryRepository factoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserFactoryMappingRepository userFactoryMappingRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EmailService emailService;

    @Autowired
    private FactoryProductionRepository factoryProductionRepository;

    @Autowired
    private FactoryInventoryStockRepository factoriesInventoryStockRepository;

    //    private final BayRepository bayRepository;
//        private final UserRepository userRepository;
//        private final UserFactoryMappingRepository userFactoryMappingRepository;
    @Override
    @Transactional
    public ApiResponse<String> createBay(Long plantHeadId, BayRequestDto request) {
        // 1️⃣ Validate Plant Head existence
        User plantHead = userRepository.findById(plantHeadId)
                .orElseThrow(() -> new RuntimeException("Plant Head not found"));

        // 2️⃣ Ensure Plant Head is actually assigned to a factory
        Optional<UserFactoryMapping> optionalMapping = userFactoryMappingRepository.findByUser(plantHead);
        if (optionalMapping.isEmpty() || optionalMapping.get().getFactory() == null) {
            throw new RuntimeException("Bay cannot be created — Plant Head is not mapped to any factory");
        }

        Factory factory = optionalMapping.get().getFactory();

        // 3️⃣ Validate that bay name doesn’t already exist in the same factory
        boolean exists = bayRepository.existsByNameAndFactory(request.getBayName(), factory);
        if (exists) {
            throw new RuntimeException("A bay with this name already exists in the factory");
        }

        // 4️⃣ Create new bay only if all checks pass
        Bay bay = new Bay();
        bay.setName(request.getBayName());
        bay.setFactory(factory);
        bay.setCreatedAt(LocalDateTime.now());
        bay.setUpdatedAt(LocalDateTime.now());

        bayRepository.save(bay);

        return new ApiResponse<>(true, "Bay created successfully for factory: " + factory.getName(), bay.getName());
    }



    @Override
    public ApiResponse<List<BayListdto>> getBaysInFactory() {
        // 🔹 Get current logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 🔹 Find plant head by email
        User plantHead = userRepository.findByEmail(email);
        if (plantHead == null) {
            throw new RuntimeException(" Plant Head not found");
        }

        // 🔹 Find factory mapped to this plant head
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(plantHead)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));

        Factory factory = mapping.getFactory();

        // 🔹 Fetch bays for this factory
        List<Bay> bays = bayRepository.findByFactory(factory);

        // 🔹 Convert to DTO
        List<BayListdto> bayDtos = bays.stream()
                .map(bay -> {
                    BayListdto dto = new BayListdto();
                    dto.setBayId(bay.getId());
                    dto.setBayName(bay.getName());
                    dto.setFactoryId(factory.getId());
                    return dto;
                })
                .toList();

        return new ApiResponse<>(true, "Bays fetched successfully", bayDtos);
    }


    @Override
    public ApiResponse<UserResponseDto> createEmployeeForCurrentPlantHead(EmployeeRequestDto request)
    {

        // 1️⃣ Get logged-in Plant Head
        String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User plantHead = userRepository.findByEmail(loggedInEmail);
        if (plantHead == null) {
            throw new RuntimeException("Logged-in Plant Head not found");
        }

        // 2️⃣ Verify that the Plant Head is mapped to a factory
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(plantHead)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));
        Factory factory = mapping.getFactory();

        // 3️⃣ Ensure only one Chief Supervisor per factory
        if (request.getRole() == Role.CHIEF_SUPERVISOR) {
            boolean exists = userFactoryMappingRepository.existsByFactoryAndAssignedRole(factory, Role.CHIEF_SUPERVISOR);
            if (exists) {
                throw new RuntimeException("This factory already has a Chief Supervisor");
            }
        }

        // 4️⃣ Check for duplicate email
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("User with this email already exists");
        }

        // 5️⃣ Create new user
        User newUser = new User();
        newUser.setUsername(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode("12345678"));
        newUser.setRole(request.getRole());
        newUser.setIsActive(Account_Status.ACTIVE);

        // Optional: add image if sent in request


        userRepository.save(newUser);

        // 6️⃣ Create mapping for the new employee
        UserFactoryMapping employeeMapping = new UserFactoryMapping();
        employeeMapping.setUser(newUser);
        employeeMapping.setFactory(factory);
        employeeMapping.setAssignedRole(request.getRole());

        Bay bay = null;
        if (request.getRole() == Role.WORKER && request.getBayId() != null) {
            bay = bayRepository.findById(request.getBayId())
                    .orElseThrow(() -> new RuntimeException("Bay not found"));

            if (!bay.getFactory().getId().equals(factory.getId())) {
                throw new RuntimeException("Bay does not belong to this factory");
            }

            employeeMapping.setBayId(bay);
        }

        userFactoryMappingRepository.save(employeeMapping);

        // 7️⃣ Send email
        sendEmailToEmployee(newUser, factory, request.getRole(), bay);

        // 8️⃣ Build response DTO
        UserResponseDto responseDto = new UserResponseDto(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getRole().name(),
                factory.getName(),
                bay != null ? bay.getName() : null,
                newUser.getImg() // ✅ Added image to response
        );

        return new ApiResponse<>(
                true,
                "Employee (" + request.getRole().name() + ") created and email sent successfully",
                responseDto
        );
    }


    private void sendEmailToEmployee(User user, Factory factory, Role role, Bay bay) {
        String subject = "Welcome to " + factory.getName();
        StringBuilder message = new StringBuilder();
        message.append("Hello ").append(user.getUsername()).append(",\n\n")
                .append("Your account has been created in Factory: ").append(factory.getName()).append(".\n")
                .append("Role: ").append(role.name()).append("\n")
                .append("Email: ").append(user.getEmail()).append("\n")
                .append("Password: 12345678\n");

        if (role == Role.WORKER && bay != null) {
            message.append("Assigned Bay: ").append(bay.getName()).append("\n");
        }

        message.append("\nPlease add your profile after logging in.\n\n")
                .append("Regards,\nSupply Chain Management Team");

        emailService.sendEmail(user.getEmail(), subject, message.toString());
    }


    @Override
    public ApiResponse<Page<UserResponseDto>> getEmployeesInFactory(
            String keyword, String roleStr, int page, int size
    ) {
        // ✅ 1. Get logged-in Plant Head
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User plantHead = userRepository.findByEmail(email);
        if (plantHead == null) {
            throw new RuntimeException("Plant Head not found");
        }

        // ✅ 2. Verify Plant Head is mapped to a factory
        Factory factory = userFactoryMappingRepository.findByUser(plantHead)
                .map(UserFactoryMapping::getFactory)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));

        // ✅ 3. Parse role filter (optional)
        Role role = null;
        if (roleStr != null && !roleStr.isBlank()) {
            try {
                role = Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role provided: " + roleStr);
            }
        }

        // ✅ 4. Build dynamic specification
        Specification<UserFactoryMapping> spec = Specification.allOf(
                EmployeeSpecifications.belongsToFactory(factory),
                EmployeeSpecifications.hasRole(role),
                EmployeeSpecifications.searchByKeyword(keyword)
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("user.username").ascending());

        // ✅ 5. Fetch paginated employee data
        Page<UserFactoryMapping> mappings = userFactoryMappingRepository.findAll(spec, pageable);

        // ✅ 6. Map entity -> DTO (now includes image)
        Page<UserResponseDto> response = mappings.map(mapping -> {
            User user = mapping.getUser();
            return new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    mapping.getAssignedRole() != null ? mapping.getAssignedRole().toString() : "N/A",
                    mapping.getFactory() != null ? mapping.getFactory().getName() : null,
                    mapping.getBayId() != null ? mapping.getBayId().getName() : null,
                    user.getImg() != null ? user.getImg() : null // ✅ add image here
            );
        });

        return new ApiResponse<>(true, "Employees fetched successfully", response);
    }



    @Override
    @Transactional
    public ApiResponse<Void> updateFactoryProductStock(UpdateStockRequestDto request) {
        // ✅ 1. Get logged-in Plant Head
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User plantHead = userRepository.findByEmail(email);
        if (plantHead == null) {
            throw new RuntimeException("Plant Head not found");
        }

        // ✅ 2. Verify that the user is mapped to a factory
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(plantHead)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));
        Factory factory = mapping.getFactory();

        // ✅ 3. Validate Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ 4. Find existing or create new stock record
        FactoriesInventoryStock stock = factoriesInventoryStockRepository
                .findByFactoryAndProduct(factory, product)
                .orElse(new FactoriesInventoryStock(null,factory, product, 0, plantHead));

        // ✅ 5. Update stock quantity
        stock.setQty(stock.getQty() + request.getQuantityProduced());
        stock.setAddedBy(plantHead);
        factoriesInventoryStockRepository.save(stock);

        // ✅ 6. Log production entry
        FactoryProduction production = new FactoryProduction();
        production.setFactory(factory);
        production.setProduct(product);
        production.setProducedQty(request.getQuantityProduced());

        factoryProductionRepository.save(production);

        return new ApiResponse<>(true, "Factory product stock updated successfully", null);
    }
    @Override
    public ApiResponse<List<FactoryProductStockResponseDto>> getAllProductsWithStock() {
        // ✅ 1. Get currently logged-in Plant Head
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User plantHead = userRepository.findByEmail(email);
        if (plantHead == null) {
            throw new RuntimeException("Plant Head not found");
        }

        // ✅ 2. Get factory assigned to plant head
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(plantHead)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));

        Factory factory = mapping.getFactory();

        // ✅ 3. Get all products from owner (assumed global)
        List<Product> allProducts = productRepository.findAll();

        // ✅ 4. Get stock entries for that factory
        List<FactoriesInventoryStock> factoryStocks = factoriesInventoryStockRepository.findAllByFactory(factory);

        // ✅ 5. Map Product + Stock
        List<FactoryProductStockResponseDto> result = allProducts.stream().map(product -> {
            Integer qty = factoryStocks.stream()
                    .filter(s -> s.getProduct().getId().equals(product.getId()))
                    .map(FactoriesInventoryStock::getQty)
                    .findFirst()
                    .orElse(0);

            return new FactoryProductStockResponseDto(
                    product.getId(),
                    product.getName(),
                    product.getCategory().getCategoryName(),
                    product.getPrice(),
                    product.getThreshold(),
                    qty,
                    product.getImage(),
                    product.getRewardPts()
            );
        }).toList();

        return new ApiResponse<>(true, "Products with factory stock fetched successfully", result);
    }
    @Override
    public ApiResponse<List<FactoryProductStockResponseDto>> getLowStockProducts() {
        // ✅ 1. Get logged-in Plant Head
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User plantHead = userRepository.findByEmail(email);
        if (plantHead == null) {
            throw new RuntimeException("Plant Head not found");
        }

        // ✅ 2. Verify mapping to factory
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(plantHead)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));
        Factory factory = mapping.getFactory();

        // ✅ 3. Get all global products (added by owner)
        List<Product> allProducts = productRepository.findAll();

        // ✅ 4. Get stock entries for that factory
        List<FactoriesInventoryStock> factoryStocks = factoriesInventoryStockRepository.findAllByFactory(factory);

        // ✅ 5. Combine both: calculate low stock products
        List<FactoryProductStockResponseDto> lowStockProducts = allProducts.stream()
                .map(product -> {
                    // Try to find stock entry for this product
                    FactoriesInventoryStock stock = factoryStocks.stream()
                            .filter(s -> s.getProduct().getId().equals(product.getId()))
                            .findFirst()
                            .orElse(null);

                    // Use 0 if no record exists
                    int qty = (stock != null && stock.getQty() != null) ? stock.getQty() : 0;
                    Long threshold = product.getThreshold();

                    return new FactoryProductStockResponseDto(
                            product.getId(),
                            product.getName(),
                            product.getCategory().getCategoryName(),
                            product.getPrice(),
                            threshold,
                            qty,
                            product.getImage(),
                            product.getRewardPts()
                    );
                })
                // ✅ Filter only those below or equal to threshold
                .filter(dto -> dto.getThreshold() != null && dto.getCurrentQty() <= dto.getThreshold())
                .collect(Collectors.toList());

        return new ApiResponse<>(true, "Low stock products fetched successfully", lowStockProducts);
    }

}
