package com.project.supply.chain.management.ServiceImplementations;
import com.project.supply.chain.management.Repositories.CentralOfficeRepository;
import com.project.supply.chain.management.Repositories.FactoryRepository;
import com.project.supply.chain.management.Repositories.UserFactoryMappingRepository;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.FactoryService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.AddEmployeeDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.FactoryDto;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import com.project.supply.chain.management.specifications.FactorySpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FactoryServiceImpl implements FactoryService {
    @Autowired
    CentralOfficeRepository centralOfficeRepository;
    @Autowired
    FactoryRepository factoryRepository;
    @Autowired
    UserRepository userRepository;
@Autowired
  EmailService emailService;
    @Autowired
    UserFactoryMappingRepository userFactoryMappingRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public ApiResponse<Void> createFactory(FactoryDto dto) {
        if (dto.getPlantHeadEmail() == null || dto.getPlantHeadEmail().isBlank()) {
            return new ApiResponse<>(false, "Plant head email is required", null);
        }

        User existingUser = userRepository.findByEmail(dto.getPlantHeadEmail());
        if (existingUser == null) {
            return new ApiResponse<>(false,
                    "No user found with this email. Please create a Plant Head first.",
                    null);
        }

        if (existingUser.getRole() != Role.PLANT_HEAD) {
            return new ApiResponse<>(false,
                    "User exists but is not a Plant Head. Please assign correct role or create new Plant Head.",
                    null);
        }

        Factory factory = new Factory();
        factory.setName(dto.getName());
        factory.setCity(dto.getCity());
        factory.setAddress(dto.getAddress());
        factory.setPlanthead(existingUser);
        factory.setIsActive(Account_Status.ACTIVE);
        //factory.setCentralOffice();
        factoryRepository.save(factory);

        UserFactoryMapping mapping = new UserFactoryMapping();
        mapping.setUser(existingUser);
        mapping.setFactory(factory);
        userFactoryMappingRepository.save(mapping);

        return new ApiResponse<>(true, "Factory created successfully", null);
    }

@Override
@Transactional
public ApiResponse<Void> createEmployeeAsPlantHead(AddEmployeeDto dto) {

    if (userRepository.findByEmail(dto.getEmail()) != null) {
        return new ApiResponse<>(false, "User with this email already exists", null);
    }

    // Step 2: Create Plant Head
    String defaultPassword = "12345678";
    User user = new User();
    user.setEmail(dto.getEmail());
    user.setUsername(dto.getUsername());
    user.setPassword(passwordEncoder.encode(defaultPassword));
    user.setRole(Role.PLANT_HEAD);
    user.setPhone(dto.getPhone());
    user.setIsActive(Account_Status.ACTIVE);
    userRepository.save(user);

    // Step 3: Send Email Notification
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

    return new ApiResponse<>(true, "Plant Head created and email sent successfully", null);
}


    @Override
    public ApiResponse<Page<FactoryDto>> getAllFactories(String search, Pageable pageable) {
        // Step 1: Fetch filtered and paginated factories with ACTIVE status
        Page<Factory> factoryPage = factoryRepository.findAll(
                FactorySpecifications.searchFactories(search)
                        .and(FactorySpecifications.isActiveFilter(Account_Status.ACTIVE)),  // Added filter for ACTIVE status
                pageable
        );

        // Step 2: Convert Factory -> FactoryDto
        Page<FactoryDto> dtoPage = factoryPage.map(factory -> {
            FactoryDto dto = new FactoryDto();
            dto.setName(factory.getName());
            dto.setCity(factory.getCity());
            dto.setAddress(factory.getAddress());
            dto.setPlantHeadEmail(factory.getPlanthead() != null ? factory.getPlanthead().getEmail() : null);
            return dto;
        });

        // Step 3: Return standardized ApiResponse
        return new ApiResponse<>(true, "Factories fetched successfully", dtoPage);
    }

    @Override
    @Transactional
    public ApiResponse<Void> updateFactory(Long factoryId, FactoryDto updateFactoryDto) {
        // Fetch the factory to be updated

        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new RuntimeException("Factory not found"));
if(factory.getIsActive()!= Account_Status.ACTIVE)
{
    throw new RuntimeException(" Factory Not found");
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
                return new ApiResponse<>(false, "Provided plant head is either invalid or not a PLANT_HEAD", null);
            }
        }

        // Save the updated factory
        factoryRepository.save(factory);

        return new ApiResponse<>(true, "Factory updated successfully", null);

}

    @Override
    @Transactional
    public ApiResponse<Void> deleteFactory(Long factoryId) {
        // Step 1: Check if the factory exists
        Optional<Factory> factoryOpt = factoryRepository.findById(factoryId);

        if (!factoryOpt.isPresent()) {
            return new ApiResponse<>(false, "Factory not found", null);
        }

        // Step 2: Update the factory's isActive status and updatedAt field
        Factory factory = factoryOpt.get();
        factory.setIsActive(Account_Status.IN_ACTIVE);
        factory.setUpdatedAt(LocalDateTime.now());  // Update the timestamp
        factoryRepository.save(factory);

        return new ApiResponse<>(true, "Factory status updated to IN_ACTIVE", null);
    }
}




