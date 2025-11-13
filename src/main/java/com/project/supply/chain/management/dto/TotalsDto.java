package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalsDto {
    private int totalProducts;
    private int totalTools;
    private int totalEmployees;
    private int activeEmployees;
    private int inactiveEmployees;
}