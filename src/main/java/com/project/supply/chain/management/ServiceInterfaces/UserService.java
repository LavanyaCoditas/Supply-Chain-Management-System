package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    public SignupResponseDto registerUser(UserSignupDto user) throws IOException;
    public LoginResponseDto loginUser(LoginDto loginDto);
    ApiResponseDto<Page<UserListDto>> getAllEmployees(
            String search,
            String role,
            Long factoryId,
            int page,
            int size,
            String sortBy,
            String sortDir
    );
    ApiResponseDto<ProfileResponseDto> getProfile(String email);
    ImageResponseDto uploadProfileImage(MultipartFile image) throws Exception;
    boolean isUserAuthorized(Long userId, String email);
    ApiResponseDto<Void> softDeleteEmployee(Long employeeId);

}
