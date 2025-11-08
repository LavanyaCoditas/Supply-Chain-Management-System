package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {
    private String username;
    private String email;
    private Long phone;
    private Role role;
    private String imageUrl;
}
