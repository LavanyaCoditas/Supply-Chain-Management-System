package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.CentralOfficeRepository;
import com.project.supply.chain.management.Repositories.UserCentralOfficeRepository;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.CentralOfficeService;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.CentralOffice;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserCentralOfficeMapping;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CentralOfficeServiceImpl implements CentralOfficeService {

   private final CentralOfficeRepository centralOfficeRepository;

   private final   UserRepository userRepository;

   private final UserCentralOfficeRepository mappingRepository;

   private final  PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiResponseDto<Void> createCentralOffice(CentralOfficeDto dto) {
        if (centralOfficeRepository.count() > 0) {
            return new ApiResponseDto<>(false, "A Central Office already exists in the system", null);
        }

        if (dto.getCentralOfficeHeadEmail() == null || dto.getCentralOfficeHeadEmail().isBlank()) {
            return new ApiResponseDto<>(false, "Central Office head email is required", null);
        }

        CentralOffice office = new CentralOffice();
        office.setLocation(dto.getLocation() != null ? dto.getLocation() : "Headquarters");
        centralOfficeRepository.save(office);

        User user = userRepository.findByEmail(dto.getCentralOfficeHeadEmail());

        if (user == null) {

            user = new User();
            user.setEmail(dto.getCentralOfficeHeadEmail());
            user.setUsername(dto.getCentralOfficeHeadName() != null ? dto.getCentralOfficeHeadName() : dto.getCentralOfficeHeadEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "default123"));
            user.setRole(Role.CENTRAL_OFFICE);
            userRepository.save(user);  // This persists the new user in the DB
        } else if (user.getRole() != Role.CENTRAL_OFFICE) {
            return new ApiResponseDto<>(false, "User exists but is not a Central Office user", null);
        }

        // Map this officer to the single central office
        UserCentralOfficeMapping mapping = new UserCentralOfficeMapping();
        mapping.setOffice(office);
        mapping.setUser(user);
        mappingRepository.save(mapping);  // This persists the mapping in the DB

        return new ApiResponseDto<>(true, "Central Office created successfully", null);
    }

    @Transactional
    @Override
    public ApiResponseDto<Void> addCentralOfficerToOffice(AddCentralOfficerDto dto) {
        //  Check if the Central Office exists
        CentralOffice office = centralOfficeRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Central Office not found"));

        //  Check if the User (Central Officer) exists and is a Central Officer
        User officer = userRepository.findByEmail(dto.getCentralOfficerEmail());
        if (officer != null) {
            // If the user exists, check if they are already a Central Officer
            if (officer.getRole() == Role.CENTRAL_OFFICE) {
                return new ApiResponseDto<>(false, "User is already a Central Officer", null);
            }
            // If the user exists but is not a Central Officer, return an error
            return new ApiResponseDto<>(false, "User already exists but is not a Central Officer", null);
        }

        //  If the user does not exist, create a new Central Officer
        officer = new User();
        officer.setEmail(dto.getCentralOfficerEmail()); officer.setUsername(dto.getCentralOfficeHeadName() != null ? dto.getCentralOfficeHeadName() : dto.getCentralOfficerEmail());
        officer.setPassword(passwordEncoder.encode("default123"));
        officer.setRole(Role.CENTRAL_OFFICE);
        officer.setPhone(dto.getPhone());
        userRepository.save(officer);

        //  Map the newly created officer to the Central Office
        UserCentralOfficeMapping mapping = new UserCentralOfficeMapping();
        mapping.setOffice(office);
        mapping.setUser(officer);
        mappingRepository.save(mapping);

        return new ApiResponseDto<>(true, "Central Officer added to Central Office successfully", null);
    }

    @Override
    public ApiResponseDto<List<CentralOfficeResponseDto>> getCentralOffice() {
        List<CentralOffice> offices = centralOfficeRepository.findAll();

        // Convert Entity → DTO
        List<CentralOfficeResponseDto> officeDtos = offices.stream().map(office -> {
            CentralOfficeResponseDto dto = new CentralOfficeResponseDto();
            dto.setId(office.getCentralOfficeId());
            dto.setLocation(office.getLocation());

            // Map all users (officers) related to this office
            if (office.getUserMappings() != null && !office.getUserMappings().isEmpty()) {
                List<UserListDto> officers = office.getUserMappings().stream()
                        .map(mapping -> {
                            User user = mapping.getUser();
                            UserListDto userDto = new UserListDto();
                            userDto.setId(user.getId());
                            userDto.setUsername(user.getUsername());
                            userDto.setEmail(user.getEmail());
                            userDto.setRole(user.getRole());
                            userDto.setIsActive(user.getIsActive());
                            userDto.setImg(user.getImg());
                            userDto.setPhone(user.getPhone());
                            return userDto;
                        })
                        .toList();
                dto.setOfficers(officers);
            } else {
                dto.setOfficers(List.of());
            }

            return dto;
        }).toList();

        return new ApiResponseDto<>(true, "Central offices fetched successfully", officeDtos);
    }



}
