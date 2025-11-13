package com.project.supply.chain.management.ServiceImplementations;

import com.cloudinary.Cloudinary;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.User;

import com.project.supply.chain.management.util.AuthUtil;
import com.project.supply.chain.management.specifications.UserSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    AuthUtil authUtil;
    @Autowired
    private Cloudinary cloudinary;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean isUserAuthorized(Long userId, String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return false;
        return user.getId().equals(userId);
    }

    @Override
    public SignupResponseDto registerUser(UserSignupDto userSignupDto) throws IOException, IOException {
        User user = new User();
        user.setUsername(userSignupDto.getUsername());
        user.setEmail(userSignupDto.getEmail());
        user.setPhone(userSignupDto.getPhone());
        user.setRole(Role.DISTRIBUTOR);
        user.setPassword(passwordEncoder.encode(userSignupDto.getPassword()));
        userRepository.save(user);

        SignupResponseDto responseDto = new SignupResponseDto();
        responseDto.setMessage("Distributor registered successfully");
        responseDto.setEmail(user.getEmail());
        responseDto.setRole(user.getRole());
        return responseDto;
    }

    @Override
    public LoginResponseDto loginUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail());

        if (user == null) {
            throw new RuntimeException("USer not found ");
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(
                true,
                token,
                "login successfull",
                user.getUsername(),
                user.getRole()
        );
    }

    //get all employees with filter and search
    @Override
    public ApiResponseDto<Page<UserListDto>> getAllEmployees(String search, String role, Long factoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Specification<User> spec = UserSpecifications.withFilters(search, role, factoryId);

        Page<User> usersPage = userRepository.findAll(spec, pageable);

        Page<UserListDto> userDtos = usersPage.map(user ->
                new UserListDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.getIsActive(),
                        user.getImg(),
                        user.getPhone()
                )
        );

        return new ApiResponseDto<>(true, "Employees fetched successfully", userDtos);
    }

    @Override
    public ApiResponseDto<ProfileResponseDto> getProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ApiResponseDto<>(false, "User not found", null);
        }

        ProfileResponseDto profile = new ProfileResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getImg()
        );

        return new ApiResponseDto<>(true, "Profile fetched successfully", profile);
    }


    @Override
    public ImageResponseDto uploadProfileImage(MultipartFile file) throws IOException {
        // ✅ Extract email from JWT (Spring Security context)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found for email: " + email);
        }

        // ✅ Upload image to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(file);

        // ✅ Save new image URL in DB
        user.setImg(imageUrl);
        userRepository.save(user);

        return new ImageResponseDto(imageUrl, "Profile image uploaded successfully");
    }

    @Override
    @Transactional
    public ApiResponseDto<Void> softDeleteEmployee(Long employeeId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByEmail(email);
        if (loggedInUser == null) {
            throw new RuntimeException("not found user");
        }

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        validateDeletePermission(loggedInUser, employee);

        employee.setIsActive(Account_Status.IN_ACTIVE);
        userRepository.save(employee);

        return new ApiResponseDto<>(true, "Employee marked as inactive successfully", null);
    }

    private void validateDeletePermission(User deleter, User target) {
        Role deleterRole = deleter.getRole();
        Role targetRole = target.getRole();

        // Owner can delete anyone
        if (deleterRole == Role.OWNER) return;

        switch (targetRole) {
            case CENTRAL_OFFICE:
                throw new RuntimeException("Only owner can delete a central officer");

            case PLANT_HEAD:
                throw new RuntimeException("Only owner can delete a plant head");

            case CHIEF_SUPERVISOR:
                if (deleterRole == Role.PLANT_HEAD && sameFactory(deleter, target))
                    return;
                throw new RuntimeException("You are not authorized to delete this chief supervisor");

            case WORKER:
                if ((deleterRole == Role.PLANT_HEAD || deleterRole == Role.CHIEF_SUPERVISOR)
                        && sameFactory(deleter, target))
                    return;
                throw new RuntimeException("You are not authorized to delete this worker");

            default:
                throw new RuntimeException("Invalid role or unauthorized operation");
        }
    }


    private boolean sameFactory(User u1, User u2) {
        if (u1.getFactoryMappings() == null || u2.getFactoryMappings() == null)
            return false;

        // Collect all factory IDs for both users
        List<Long> user1FactoryIds = u1.getFactoryMappings().stream()
                .filter(mapping -> mapping.getFactory() != null)
                .map(mapping -> mapping.getFactory().getId())
                .toList();

        List<Long> user2FactoryIds = u2.getFactoryMappings().stream()
                .filter(mapping -> mapping.getFactory() != null)
                .map(mapping -> mapping.getFactory().getId())
                .toList();

        // Return true if they share at least one factory
        for (Long factoryId1 : user1FactoryIds) {
            if (user2FactoryIds.contains(factoryId1)) {
                return true;
            }
        }

        return false;

    }
}






