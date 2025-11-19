package com.project.supply.chain.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ToolInventoryStockDto {

    private Long toolId;
    @NotNull(message = "quantity must be entered")
    private Long quantity;
}
