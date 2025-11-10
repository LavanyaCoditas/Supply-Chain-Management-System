package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.FactoryProductStockResponseDto;
import com.project.supply.chain.management.dto.ImageResponseDto;
import com.project.supply.chain.management.dto.ProfileResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile/users")
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping("/get/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName(); // email extracted from token

        ApiResponse<ProfileResponseDto> response = userService.getProfile(currentUserEmail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ImageResponseDto> uploadProfileImage(
            @RequestParam("image") MultipartFile image
    ) throws Exception {
        ImageResponseDto response = userService.uploadProfileImage(image);
        return ResponseEntity.ok(response);
    }

}
