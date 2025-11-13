package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSummaryDto {
    private String productName;
    private Integer producedQuantity;
}
