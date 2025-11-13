package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceImplementations.AuthService;
import com.project.supply.chain.management.ServiceImplementations.CloudinaryService;

import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController

{
    @Autowired
    private UserService userService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody UserSignupDto userSignupDto) throws IOException {
        SignupResponseDto responseDto = userService.registerUser(userSignupDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public  ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto)
    {

        return ResponseEntity.ok(userService.loginUser(loginDto));
    }

    @Autowired
    private AuthService authService;

    @PostMapping("/logout")
    public ApiResponseDto<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        return authService.logout(token);
    }
    }



