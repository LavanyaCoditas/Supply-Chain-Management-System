package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceImplementations.CloudinaryService;
import com.project.supply.chain.management.ServiceImplementations.UserServiceImpl;

import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.dto.LoginDto;
import com.project.supply.chain.management.dto.LoginResponseDto;
import com.project.supply.chain.management.dto.SignupResponseDto;
import com.project.supply.chain.management.dto.UserSignupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    }



