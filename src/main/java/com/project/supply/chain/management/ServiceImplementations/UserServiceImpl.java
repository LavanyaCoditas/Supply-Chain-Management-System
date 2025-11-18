package com.project.supply.chain.management.ServiceImplementations;
import com.cloudinary.Cloudinary;
import com.project.supply.chain.management.Repositories.UserFactoryMappingRepository;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import com.project.supply.chain.management.exceptions.*;
import com.project.supply.chain.management.util.ApplicationUtils;
import com.project.supply.chain.management.util.AuthUtil;
import com.project.supply.chain.management.specifications.UserSpecifications;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CloudinaryService cloudinaryService;

    private  final AuthUtil authUtil;

    private  final UserFactoryMappingRepository userFactoryMappingRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ApplicationUtils appUtils;

    @Override
    public boolean isUserAuthorized(Long userId, String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return false;
        return user.getId().equals(userId);
    }

    @Override
    public SignupResponseDto registerUser(UserSignupDto userSignupDto)  {
        if (userRepository.existsByEmail(userSignupDto.getEmail())) {
            throw new EmailAlreadyExistException("Email '" + userSignupDto.getEmail() + "' already exists");
        }

        if (userRepository.existsByUsername(userSignupDto.getUsername()))
        {
            throw new UsernameAlreadyExistException("Username "+ userSignupDto.getUsername()+" already exists");
        }
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
        log.info("User Signed Up succesfully {} {}", user.getEmail(),user.getUsername());
        return responseDto;

    }

    @Override
    public LoginResponseDto loginUser(LoginDto loginDto) {
        String email = loginDto.getEmail();
        User user = appUtils.getUser(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password entered doesn't match saved password");
        }
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(
                true,
                token,
                "login successfully complete",
                user.getUsername(),
                user.getRole()
        );
    }


    @Override
    public ApiResponseDto<Page<UserListDto>> getAllEmployees(String search, String role, Long factoryId, int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = UserSpecifications.withFilters(search, role, factoryId);

        Page<User> usersPage = userRepository.findAll(spec, pageable);

        Page<UserListDto> userDtos = usersPage.map(user -> {

            // Find factory for user
            Optional<UserFactoryMapping> mapping = userFactoryMappingRepository.findByUser(user);

            Long mappedFactoryId = null;
            String mappedFactoryName = null;

            if (mapping.isPresent()) {
                mappedFactoryId = mapping.get().getFactory().getId();
                mappedFactoryName = mapping.get().getFactory().getName();
            }

            return new UserListDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getIsActive(),
                    user.getImg(),
                    user.getPhone(),
                    mappedFactoryId,
                    mappedFactoryName
            );
        });

        return new ApiResponseDto<>(true, "Employees fetched successfully", userDtos);
    }


    @Override
    public ApiResponseDto<ProfileResponseDto> getProfile(String email) {

        User user =appUtils.getUser(email);
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

        String email = appUtils.getLoggedInUserEmail();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found for email: " + email);
        }


        String imageUrl = cloudinaryService.uploadImage(file);
        user.setImg(imageUrl);
        userRepository.save(user);

        return new ImageResponseDto(imageUrl, "Profile image uploaded successfully");
    }

    @Override
    @Transactional
    public ApiResponseDto<Void> softDeleteEmployee(Long employeeId) {
        String email = appUtils.getLoggedInUserEmail();
        User loggedInUser = userRepository.findByEmail(email);
        if (loggedInUser == null) {
            throw new UserNotFoundException("User not found");
        }

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new UserNotFoundException("Employee not found"));

        validateDeletePermission(loggedInUser, employee);

        employee.setIsActive(Account_Status.IN_ACTIVE);
        userRepository.save(employee);

        return new ApiResponseDto<>(true, "Employee marked as inactive successfully", null);
    }

    private void validateDeletePermission(User deleter, User target) {
        Role deleterRole = deleter.getRole();
        Role targetRole = target.getRole();

        if (deleterRole == Role.OWNER) return;

        switch (targetRole) {
            case CENTRAL_OFFICE:
                throw new UnauthorizedAccessException("Only owner can delete a central officer");

            case PLANT_HEAD:
                throw new UnauthorizedAccessException("Only owner can delete a plant head");

            case CHIEF_SUPERVISOR:
                if (deleterRole == Role.PLANT_HEAD && sameFactory(deleter, target))
                    return;
                throw new UnauthorizedAccessException("You are not authorized to delete this chief supervisor");

            case WORKER:
                if ((deleterRole == Role.PLANT_HEAD || deleterRole == Role.CHIEF_SUPERVISOR)
                        && sameFactory(deleter, target))
                    return;
                throw new UnauthorizedAccessException("You are not authorized to delete this worker");

            default:
                throw new UnauthorizedAccessException("Invalid role or unauthorized operation");
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

        for (Long factoryId1 : user1FactoryIds)
        {
            if (user2FactoryIds.contains(factoryId1)) {
                return true;
            }
        }
        return false;
    }
}






