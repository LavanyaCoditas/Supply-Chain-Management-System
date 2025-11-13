package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PlantHeadDashboardDto {
    private Long factoryId;
    private String factoryName;
    private TotalsDto totals;
    private List<ProductSummaryDto> productionSummary;
    private List<EmployeeOverviewDto> employeeOverview;
}
