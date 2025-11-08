package com.project.supply.chain.management.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDto {
    private String username;
    private Long phone;
    private String imageUrl;
}
