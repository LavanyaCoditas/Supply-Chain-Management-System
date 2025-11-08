package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto
{
    private boolean success;
    private String token;
    private String message;
    private String username;
    private Role role;


}
