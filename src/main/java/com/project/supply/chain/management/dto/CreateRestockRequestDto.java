package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestockRequestDto {
    private Long factoryId;
    private Long productId;
    private Integer qtyRequested;
}