package com.project.supply.chain.management.ServiceImplementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.User;

import com.project.supply.chain.management.util.AuthUtil;
import com.project.supply.chain.management.specifications.UserSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

    // Setting user role, assuming only 'DISTRIBUTOR' for now
    user.setRole(Role.DISTRIBUTOR);

    // Encrypt the password
    user.setPassword(passwordEncoder.encode(userSignupDto.getPassword()));

    // Upload image to Cloudinary (returns the image URL)
//    String imageUrl = cloudinaryService.uploadImage((MultipartFile) userSignupDto.getProfileImage().getResource().getFile());
//    MultipartFile file = userSignupDto.getProfileImage();
//    String imageUrl = cloudinaryService.uploadImage(file);
//    // Set the profile image URL
//    user.setImg(imageUrl);

    // Save user to the database
    userRepository.save(user);

    // Create and return response DTO
    SignupResponseDto responseDto = new SignupResponseDto();
    responseDto.setMessage("Distributor registered successfully");
    responseDto.setEmail(user.getEmail());
    responseDto.setRole(user.getRole());
//    responseDto.setImageUrl(user.getImg());
    return responseDto;
}

    @Override
    public LoginResponseDto loginUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail());

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
    public ApiResponse<Page<UserListDto>> getAllEmployees(String search, String role, Long factoryId, int page, int size) {
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

        return new ApiResponse<>(true, "Employees fetched successfully", userDtos);
    }

    @Override
    public ApiResponse<ProfileResponseDto> getProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ApiResponse<>(false, "User not found", null);
        }

        ProfileResponseDto profile = new ProfileResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getImg()
        );

        return new ApiResponse<>(true, "Profile fetched successfully", profile);
    }



    @Override
    public ImageResponseDto uploadProfileImage(Long userId, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = cloudinaryService.uploadImage(file);

        user.setImg(imageUrl);
        userRepository.save(user);

        return new ImageResponseDto(imageUrl, "Profile image uploaded successfully");
    }

}


