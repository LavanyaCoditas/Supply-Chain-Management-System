package com.project.supply.chain.management.dto;
import com.project.supply.chain.management.constants.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupResponseDto
{



        private String message; // Message indicating the result of the signup
        private String email;   // Email of the user
        private Role role;

//        private String imageUrl;


}
