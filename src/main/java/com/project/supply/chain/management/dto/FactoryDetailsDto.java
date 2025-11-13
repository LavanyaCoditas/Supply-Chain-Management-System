package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class FactoryDetailsDto {
    private Long factoryId;
    private String factoryName;
    private String location;
    private Long totalEmployees;
    private List<ToolSummaryDto> tools;
    private List<ProductSummaryDto> products;
}
