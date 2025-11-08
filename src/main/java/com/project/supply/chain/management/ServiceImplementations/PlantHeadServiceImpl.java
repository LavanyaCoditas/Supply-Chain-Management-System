package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.BayRepository;
import com.project.supply.chain.management.Repositories.FactoryRepository;
import com.project.supply.chain.management.Repositories.UserFactoryMappingRepository;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.PlantHeadService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.BayRequestDto;
import com.project.supply.chain.management.dto.EmployeeRequestDto;
import com.project.supply.chain.management.entity.Bay;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        System.out.println("+++++++++++++"+bay.getName());
        bay.setFactory(factory);
        bay.setCreatedAt(LocalDateTime.now());
        bay.setUpdatedAt(LocalDateTime.now());
        bayRepository.save(bay);

        return new ApiResponse<String>(true,"bay created successfully",bay.getName());
    }


}
