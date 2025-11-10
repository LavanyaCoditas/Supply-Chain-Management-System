// 📁 com.project.supply.chain.management.ServiceImplementations.ChiefSupervisorServiceImpl.java
package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.BayRepository;
import com.project.supply.chain.management.Repositories.UserFactoryMappingRepository;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.CheifSupervisorService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.Bay;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.project.supply.chain.management.specifications.EmployeeSpecifications.hasRole;
import static com.project.supply.chain.management.specifications.WorkerSpecifications.*;

@Service
public class ChiefSupervisorServiceImpl implements CheifSupervisorService {

    @Autowired
    EmailService emailService;
 @Autowired
    BayRepository bayRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactoryMappingRepository userFactoryMappingRepository;

    @Override
    public ApiResponse<WorkerResponseDto> addWorker(AddEmployeeDto dto) {
        String supervisorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User supervisor = userRepository.findByEmail(supervisorEmail);
        if (supervisor == null) {
            throw new RuntimeException("Supervisor not found in context");
        }

        // ✅ Check duplicate email
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            return new ApiResponse<>(false, "User with this email already exists", null);
        }

        // ✅ Get supervisor’s mapping
        UserFactoryMapping supervisorMapping = userFactoryMappingRepository.findByUser(supervisor)
                .orElseThrow(() -> new RuntimeException("Supervisor is not mapped to any factory/bay"));

        // ✅ Find selected Bay
        Bay selectedBay = bayRepository.findById(dto.getBayId())
                .orElseThrow(() -> new RuntimeException("Selected bay not found"));

        // ✅ Validate: Bay must belong to same factory as supervisor
        if (!selectedBay.getFactory().getId().equals(supervisorMapping.getFactory().getId())) {
            throw new RuntimeException("Selected bay does not belong to your factory");
        }

        // ✅ Create new worker
        String defaultPassword = "default@123";
        User worker = new User();
        worker.setUsername(dto.getUsername());
        worker.setEmail(dto.getEmail());
        worker.setPhone(dto.getPhone());
        worker.setPassword(passwordEncoder.encode(defaultPassword));
        worker.setRole(Role.WORKER);
        worker.setIsActive(Account_Status.ACTIVE);

        userRepository.save(worker);

        // ✅ Create mapping for new worker
        UserFactoryMapping workerMapping = new UserFactoryMapping();
        workerMapping.setUser(worker);
        workerMapping.setFactory(supervisorMapping.getFactory());
        workerMapping.setBayId(selectedBay);
        workerMapping.setAssignedRole(Role.WORKER);
        userFactoryMappingRepository.save(workerMapping);

        // ✅ Prepare response
        WorkerResponseDto response = new WorkerResponseDto(
                worker.getId(),
                worker.getUsername(),
                worker.getEmail(),
                worker.getRole().name(),
                supervisorMapping.getFactory().getName(),
                selectedBay.getName()
        );

        // ✅ Send Email Notification to Worker
        String loginUrl = "http://localhost:8080/login"; // or your frontend login URL

        String subject = "Welcome to Supply Chain System - Worker Account Created";
        String body = String.format("""
            Hello %s,
            
            You have been successfully added as a Worker in the Supply Chain Management system.
            
            Your login credentials are as follows:
            -----------------------------------
            Username: %s
            Email: %s
            Password: %s
            -----------------------------------
            
            Factory: %s
            Bay: %s
            
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
                supervisorMapping.getFactory().getName(),
                selectedBay.getName(),
                loginUrl
        );

        emailService.sendEmail(dto.getEmail(), subject, body);

        return new ApiResponse<>(true, "Worker added successfully and email sent", response);
    }



    @Override
    public ApiResponse<WorkerResponseDto> updateWorker(Long workerId, UpdateEmployeeDto dto) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        if (dto.getUsername() != null) worker.setUsername(dto.getUsername());
        if (dto.getEmail() != null) worker.setEmail(dto.getEmail());
        if (dto.getPhone() != null) worker.setPhone(dto.getPhone());

        userRepository.save(worker);

        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(worker)
                .orElse(null);

        WorkerResponseDto response = new WorkerResponseDto(
                worker.getId(),
                worker.getUsername(),
                worker.getEmail(),
                worker.getRole().name(),
                mapping != null && mapping.getFactory() != null ? mapping.getFactory().getName() : null,
                mapping != null && mapping.getBayId() .getName()!= null ? mapping.getBayId().getName() : null
        );

        return new ApiResponse<>(true, "Worker updated successfully", response);
    }

    @Override
    public ApiResponse<Void> softDeleteWorker(Long workerId) {
        // ✅ Get logged-in supervisor
        String supervisorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User supervisor = userRepository.findByEmail(supervisorEmail);
        if (supervisor == null) {
            throw new RuntimeException("Supervisor not found in context");
        }

        // ✅ Find the worker
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // ✅ Update worker status
        worker.setIsActive(Account_Status.IN_ACTIVE);
        userRepository.save(worker);

        // ✅ Get supervisor’s mapping (for factory/bay context)
        UserFactoryMapping supervisorMapping = userFactoryMappingRepository.findByUser(supervisor)
                .orElseThrow(() -> new RuntimeException("Supervisor is not mapped to any factory/bay"));

        // ✅ Get worker mapping (to fetch factory and bay info)
        UserFactoryMapping workerMapping = userFactoryMappingRepository.findByUser(worker)
                .orElse(null);

        String factoryName = workerMapping != null && workerMapping.getFactory() != null
                ? workerMapping.getFactory().getName()
                : "N/A";

        String bayName = workerMapping != null && workerMapping.getBayId() != null
                ? workerMapping.getBayId().getName()
                : "N/A";

        // ✅ Prepare Email Notification
        String subject = "Notice: Removal from Worker Position - Supply Chain System";

        String body = String.format("""
        Hello %s,
        
        This is to inform you that you have been removed from your position as a Worker 
        in the Supply Chain Management system.
        
        Details:
        -----------------------------------
        Factory: %s
        Bay: %s
        -----------------------------------
        
        If you believe this was a mistake, please contact your supervisor (%s).
        
        Regards,
        Supply Chain Management Team
        """,
                worker.getUsername(),
                factoryName,
                bayName,
                supervisor.getEmail()
        );

        // ✅ Send Email
        emailService.sendEmail(worker.getEmail(), subject, body);

        return new ApiResponse<>(true, "Worker deleted successfully and email sent", null);
    }


    @Override
    public ApiResponse<Page<WorkerResponseDto>> searchWorkers(
            String name, String factoryName, String bayName, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Specification<UserFactoryMapping> spec = (root, query, cb) -> cb.conjunction();

        spec = spec
                .and(hasRole(Role.WORKER))
                .and(searchByWorkerName(name))
                .and(searchByFactoryName(factoryName))
                .and(searchByBayName(bayName))
                .and(hasAccountStatus(Account_Status.ACTIVE));

        Page<UserFactoryMapping> workers = userFactoryMappingRepository.findAll(spec, pageable);

        Page<WorkerResponseDto> dtoPage = workers.map(mapping -> {
            User user = mapping.getUser();
            String factory = mapping.getFactory() != null ? mapping.getFactory().getName() : null;
            String bay = mapping.getBayId().getName() != null ? mapping.getBayId().getName() : null;

            return new WorkerResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    factory,
                    bay
            );
        });


        return new ApiResponse<>(true, "Workers fetched successfully", dtoPage);
    }
}

