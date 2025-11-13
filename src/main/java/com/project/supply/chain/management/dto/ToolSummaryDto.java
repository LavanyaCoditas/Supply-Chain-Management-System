package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ToolSummaryDto {
    private String toolName;
    private Long totalQuantity;
    private Long availableQuantity;
    private Long issuedQuantity;
}
