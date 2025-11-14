package com.project.supply.chain.management.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateRestockRequestDto {
    private Long factoryId;   // 🔁 changed from String factoryName
    private Long productId;
    private Integer qtyRequested;
}
