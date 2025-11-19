package com.project.supply.chain.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductStockDto {
    @NotNull(message = "product Id is required")
    private Long productId;
    @NotNull(message = "quantity not entered please ensure to enter quantity")
    private Integer quantity;
}