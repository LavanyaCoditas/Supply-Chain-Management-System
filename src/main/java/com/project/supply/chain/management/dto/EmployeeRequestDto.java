package com.project.supply.chain.management.dto;
import com.project.supply.chain.management.constants.Role;
import lombok.Data;
@Data
public class EmployeeRequestDto {
    private String name;
    private String email;
    private Long phone;
    private Role role; // "CHIEF_SUPERVISOR" or "WORKER"
    private Long bayId;
    // Optional for worker

}
