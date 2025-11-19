package com.project.supply.chain.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateStockRequestDto {
    @NotNull(message = "product not selected/ doesn't exists try again")
    private Long productId;
    @NotNull(message = "quantity not entered")
    @Positive(message = "quantity must be positive")
    private Integer quantityProduced;
}
