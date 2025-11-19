package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactoryProductionSummaryDto {
    private String factoryName;
    private Long totalProducts;
    private Long totalProduction;

}
