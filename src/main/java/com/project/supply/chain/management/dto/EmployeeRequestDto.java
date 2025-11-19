package com.project.supply.chain.management.dto;
import com.project.supply.chain.management.constants.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class EmployeeRequestDto {
    @NotBlank(message = "name should not be blank")
    private String name;
    @NotBlank(message = "email is required")
    @Email(message = "email must be in valid format")
    private String email;
    @NotNull(message = "phone number required")
    private Long phone;
    @NotBlank(message = "please select role")
    private Role role; // CHIEF_SUPERVISOR or WORKER
    @NotNull(message = "please select a bay ")
    private Long bayId;


}
