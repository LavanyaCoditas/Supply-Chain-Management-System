package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.BayRepository;
import com.project.supply.chain.management.Repositories.FactoryRepository;
import com.project.supply.chain.management.Repositories.UserFactoryMappingRepository;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.PlantHeadService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.Bay;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
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

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class PlantHeadServiceImpl implements PlantHeadService {

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

    //    private final BayRepository bayRepository;
//        private final UserRepository userRepository;
//        private final UserFactoryMappingRepository userFactoryMappingRepository;
    @Override
    @Transactional
    public ApiResponse<String> createBay(Long plantHeadId, BayRequestDto request) {
        // 1️⃣ Fetch the Plant Head user
        User plantHead = userRepository.findById(plantHeadId)
                .orElseThrow(() -> new RuntimeException("Plant Head not found"));

        // 2️⃣ Check if the Plant Head is mapped to a factory
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(plantHead)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));

        Factory factory = mapping.getFactory();

        // 3️⃣ Create a new Bay for this factory
        Bay bay = new Bay();
        bay.setName(request.getBayName());
        System.out.println("+++++++++++++" + bay.getName());
        bay.setFactory(factory);
        bay.setCreatedAt(LocalDateTime.now());
        bay.setUpdatedAt(LocalDateTime.now());
        bayRepository.save(bay);

        return new ApiResponse<String>(true, "bay created successfully", bay.getName());
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
    public ApiResponse<UserResponseDto> createEmployeeForCurrentPlantHead(EmployeeRequestDto request) {

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

        // 4️⃣ Create user
        User newUser = new User();
        newUser.setUsername(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode("12345678"));
        newUser.setRole(request.getRole());
        newUser.setIsActive(Account_Status.ACTIVE);
        userRepository.save(newUser);

        // 5️⃣ Create mapping
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

        // 6️⃣ Send email
        sendEmailToEmployee(newUser, factory, request.getRole(), bay);

        // 7️⃣ Create response DTO
        UserResponseDto responseDto = new UserResponseDto(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getRole().name(),
                factory.getName(),
                bay != null ? bay.getName() : null
        );

        return new ApiResponse<>(
                true,
                "Mail sent to " + request.getRole().name(),
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
        // ✅ 1. Get currently logged-in user (Plant Head)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User plantHead = userRepository.findByEmail(email);
        if (plantHead == null) {
            throw new RuntimeException("Plant Head not found");
        }

        // ✅ 2. Verify Plant Head is mapped to a factory
        Factory factory = userFactoryMappingRepository.findByUser(plantHead)
                .map(UserFactoryMapping::getFactory)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));

        // ✅ 3. Parse role filter if provided
        Role role = null;
        if (roleStr != null && !roleStr.isBlank()) {
            try {
                role = Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role provided: " + roleStr);
            }
        }

        // ✅ 4. Build dynamic specifications (no .where() used)
        Specification<UserFactoryMapping> spec = Specification.allOf(
                EmployeeSpecifications.belongsToFactory(factory),
                EmployeeSpecifications.hasRole(role),
                EmployeeSpecifications.searchByKeyword(keyword)
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by("user.username").ascending());

        // ✅ 5. Fetch paginated data
        Page<UserFactoryMapping> mappings = userFactoryMappingRepository.findAll(spec, pageable);

        // ✅ 6. Map to DTO
        Page<UserResponseDto> response = mappings.map(m -> new UserResponseDto(
                m.getUser().getId(),
                m.getUser().getUsername(),
                m.getUser().getEmail(),
                m.getAssignedRole() != null ? m.getAssignedRole().toString() : "N/A",
                m.getFactory() != null ? m.getFactory().getName() : null,
                (m.getBayId() != null) ? m.getBayId().getName() : null
        ));

        return new ApiResponse<>(true, "Employees fetched successfully", response);
    }
}
