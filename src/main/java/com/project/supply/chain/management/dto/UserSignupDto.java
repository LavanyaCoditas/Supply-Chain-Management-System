package com.project.supply.chain.management.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupDto {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._-]*$", message = "Username must start with a letter and can only contain letters, numbers, dots, underscores, or hyphens")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9._-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email must start with a letter, should have @, have valid domain and be valid like example@gmail.com")
    private String email;

    //alpha numeric , must contain at leat 1 special character and min 6 to 14
    @NotBlank(message = "Password is required")
    @Size(min = 6,  message = "Password must be between 6 and 8 characters")
    private String password;


//    @NotNull(message = "Profile image cannot be null")
//    private MultipartFile profileImage;

    private Long phone;

}