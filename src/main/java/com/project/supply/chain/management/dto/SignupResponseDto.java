package com.project.supply.chain.management.dto;
import com.project.supply.chain.management.constants.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupResponseDto
{
        private String message;
        private String email;
        private Role role;
}
