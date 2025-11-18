package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CentralOfficeInventoryDto {
    private Long productId;
    private String productName;
    private Long quantity;
    private Long totalReceived;
}