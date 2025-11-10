package com.project.supply.chain.management.dto;

import lombok.Data;

@Data
public class UpdateStockRequestDto {
    private Long productId;
    private Integer quantityProduced; // number of new units produced
}
