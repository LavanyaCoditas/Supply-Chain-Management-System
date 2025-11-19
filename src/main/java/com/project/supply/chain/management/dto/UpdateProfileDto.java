package com.project.supply.chain.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDto {
    @NotBlank(message = "username is required")
    private String username;

    private Long phone;

    private String imageUrl;
}
