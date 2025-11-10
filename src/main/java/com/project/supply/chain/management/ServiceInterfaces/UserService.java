package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    public SignupResponseDto registerUser(UserSignupDto user) throws IOException;
    public LoginResponseDto loginUser(LoginDto loginDto);
    ApiResponse<Page<UserListDto>> getAllEmployees(String search, String role, Long factoryId, int page, int size);
    ApiResponse<ProfileResponseDto> getProfile(String email);
    ImageResponseDto uploadProfileImage(MultipartFile image) throws Exception;
    boolean isUserAuthorized(Long userId, String email);
}
