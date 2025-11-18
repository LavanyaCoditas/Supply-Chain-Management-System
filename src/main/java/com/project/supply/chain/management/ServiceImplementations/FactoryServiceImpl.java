package com.project.supply.chain.management.ServiceImplementations;
import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.FactoryService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.*;
import com.project.supply.chain.management.exceptions.InvalidCredentialsException;
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import com.project.supply.chain.management.exceptions.UnauthorizedAccessException;
import com.project.supply.chain.management.exceptions.UserNotFoundException;
import com.project.supply.chain.management.specifications.FactorySpecifications;
import com.project.supply.chain.management.util.ApplicationUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FactoryServiceImpl implements FactoryService {

    private final CentralOfficeRepository centralOfficeRepository;

    private final   FactoryRepository factoryRepository;

    private final UserRepository userRepository;

    private final  EmailService emailService;

    private final  UserFactoryMappingRepository userFactoryMappingRepository;

    private final ApplicationUtils appUtils;

    private final  FactoryProductionRepository factoryProductionRepository;

    private final ToolStockRepository toolStockRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public ApiResponseDto<Void> createFactory(FactoryDto dto) {

        //  Validate plant head email
        if (dto.getPlantHeadEmail() == null || dto.getPlantHeadEmail().isBlank()) {
            throw  new InvalidCredentialsException("Plant head email is required");
        }

        // Check if user exists
        User existingUser = userRepository.findByEmail(dto.getPlantHeadEmail());
        if (existingUser == null) {
            throw  new UserNotFoundException("No user found with this email. Please create a Plant Head first");
        }

        // Verify user role is PLANT_HEAD
        if (existingUser.getRole() != Role.PLANT_HEAD) {
            throw  new UnauthorizedAccessException("User exists but is not a Plant Head. Please assign correct role or create new Plant Head.");
        }

        //Check if this Plant Head is already mapped to another factory
        boolean isAlreadyAssigned = userFactoryMappingRepository.existsByUser(existingUser);
        if (isAlreadyAssigned) {
            throw  new UnauthorizedAccessException("This Plant Head is already assigned to another factory.");
        }

        //Create factory entity
        Factory factory = new Factory();
        factory.setName(dto.getName());
        factory.setCity(dto.getCity());
        factory.setAddress(dto.getAddress());
        factory.setPlanthead(existingUser);
        factory.setIsActive(Account_Status.ACTIVE);
        factoryRepository.save(factory);

        //Create factory-user mapping
        UserFactoryMapping mapping = new UserFactoryMapping();
        mapping.setUser(existingUser);
        mapping.setFactory(factory);
        userFactoryMappingRepository.save(mapping);

        return new ApiResponseDto<>(true, "Factory created successfully and assigned to Plant Head", null);
    }


    @Override
  @Transactional
  public ApiResponseDto<Void> createEmployeeAsPlantHead(AddEmployeeDto dto) {

    if (userRepository.findByEmail(dto.getEmail()) != null) {
        throw  new InvalidCredentialsException("User with this email already exists");
    }

    // Create Plant Head
    String defaultPassword = "12345678";
    User user = new User();
    user.setEmail(dto.getEmail());
    user.setUsername(dto.getUsername());
    user.setPassword(passwordEncoder.encode(defaultPassword));
    user.setRole(Role.PLANT_HEAD);
    user.setPhone(dto.getPhone());
    user.setIsActive(Account_Status.ACTIVE);
    userRepository.save(user);

    //  Send Email Notification
    String loginUrl = "http://localhost:8080/login";

    String subject = "Welcome! You are appointed as Plant Head";
    //text block usage
    String body = String.format("""
            Hello %s,
            
            Congratulations! You have been appointed as the Plant Head of the factory.
            
            Your login credentials:
            -----------------------------------
            Username: %s
            Email: %s
            Password: %s
            -----------------------------------
            
            You can log in here:
            %s
            
            Please change your password after your first login.
            
            Regards,
            Supply Chain Management Team
            """,
            dto.getUsername(),
            dto.getUsername(),
            dto.getEmail(),
            defaultPassword,
            loginUrl
    );

    emailService.sendEmail(dto.getEmail(), subject, body);

    return new ApiResponseDto<>(true, "Plant Head created and email sent successfully", null);
}


    @Override
    public ApiResponseDto<Page<FactoryDto>> getAllFactories(String search, Pageable pageable) {

        Page<Factory> factoryPage = factoryRepository.findAll(
                FactorySpecifications.searchFactories(search)
                        .and(FactorySpecifications.isActiveFilter(Account_Status.ACTIVE)),  // Added filter for ACTIVE status
                pageable
        );

        //  Convert Factory -> FactoryDto
        Page<FactoryDto> dtoPage = factoryPage.map(factory -> {
            FactoryDto dto = new FactoryDto();
dto.setFactoryId(factory.getId());
            dto.setName(factory.getName());
            dto.setCity(factory.getCity());
            dto.setAddress(factory.getAddress());
            dto.setPlantHeadEmail(factory.getPlanthead() != null ? factory.getPlanthead().getEmail() : null);
            return dto;
        });

        //Return standardized ApiResponse
        return new ApiResponseDto<>(true, "Factories fetched successfully", dtoPage);
    }

    @Override
    @Transactional
    public ApiResponseDto<Void> updateFactory(Long factoryId, FactoryDto updateFactoryDto) {
        // Fetch the factory to be updated

        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found"));
if(factory.getIsActive()!= Account_Status.ACTIVE)
{
    throw new ResourceNotFoundException(" Factory Not found");
}
        // Update factory details (except for status)
        factory.setName(updateFactoryDto.getName() != null ? updateFactoryDto.getName() : factory.getName());
        factory.setCity(updateFactoryDto.getCity() != null ? updateFactoryDto.getCity() : factory.getCity());
        factory.setAddress(updateFactoryDto.getAddress() != null ? updateFactoryDto.getAddress() : factory.getAddress());

        // If the plant head email is provided, update it
        if (updateFactoryDto.getPlantHeadEmail() != null && !updateFactoryDto.getPlantHeadEmail().isBlank()) {
            User plantHead = userRepository.findByEmail(updateFactoryDto.getPlantHeadEmail());
            if (plantHead != null && plantHead.getRole().name().equals("PLANT_HEAD")) {
                factory.setPlanthead(plantHead);
            } else {
                return new ApiResponseDto<>(false, "Provided plant head is either invalid or not a PLANT_HEAD", null);
            }
        }
        // Save the updated factory
        factoryRepository.save(factory);

        return new ApiResponseDto<>(true, "Factory updated successfully", null);
}

    @Override
    @Transactional
    public ApiResponseDto<Void> deleteFactory(Long factoryId) {
        //  Check if the factory exists
        Optional<Factory> factoryOpt = factoryRepository.findById(factoryId);

        if (!factoryOpt.isPresent()) {
            throw  new ResourceNotFoundException( "Factory not found");
        }

        // Update the factory's isActive status and updatedAt field
        Factory factory = factoryOpt.get();
        factory.setIsActive(Account_Status.IN_ACTIVE);
        factory.setUpdatedAt(LocalDateTime.now());
        factoryRepository.save(factory);

        return new ApiResponseDto<>(true, "Factory status updated to IN_ACTIVE", null);
    }
    @Override
    public ApiResponseDto<List<FactoryProductionSummaryDto>> getFactoryProductionSummary() {
        List<FactoryProductionSummaryDto> summaries = factoryProductionRepository.getFactoryProductionSummary();

        return new ApiResponseDto<>(true, "Production summary fetched successfully", summaries);
    }

    @Override
    public ApiResponseDto<FactoryDetailsDto> getFactoryDetails(Long factoryId)
    {
        User user = appUtils.getUser(appUtils.getLoggedInUserEmail());

        if (user == null)
            throw new UserNotFoundException("User not found");

        Factory factory;
        if (user.getRole() == Role.OWNER) {
            if (factoryId == null)
                throw new ResourceNotFoundException("Factory not found");

            factory = factoryRepository.findById(factoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Factory not found"));
        }

        // Plant Head automatically linked to their assigned factory
        else if (user.getRole() == Role.PLANT_HEAD) {
            UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(user)
                    .orElseThrow(() -> new UnauthorizedAccessException("Plant Head is not assigned to any factory"));

            factory = mapping.getFactory();
        }

        else {
            throw new UnauthorizedAccessException("You are not authorized to view factory details");
        }

        // Total Employees
        Long totalEmployees = userFactoryMappingRepository.countByFactory(factory);

        // Tool Details
        List<ToolStock> toolStocks = toolStockRepository.findByFactory(factory);
        List<ToolSummaryDto> toolSummaries = toolStocks.stream()
                .map(ts -> new ToolSummaryDto(
                        ts.getTool().getName(),
                        ts.getTotalQuantity(),
                        ts.getAvailableQuantity(),
                        ts.getIssuedQuantity()))
                .toList();

        // Product Details
        List<FactoryProduction> productions = factoryProductionRepository.findByFactory(factory);
        List<ProductSummaryDto> productSummaries = productions.stream()
                .map(p -> new ProductSummaryDto(
                        p.getProduct().getName(),
                        p.getProducedQty()))
                .toList();

        FactoryDetailsDto dto = new FactoryDetailsDto(
factory.getId(),
                factory.getName(),
                factory.getCity(),
                totalEmployees,
                toolSummaries,
                productSummaries
        );

        return new ApiResponseDto<>(true, "Factory details fetched successfully", dto);
    }

}




