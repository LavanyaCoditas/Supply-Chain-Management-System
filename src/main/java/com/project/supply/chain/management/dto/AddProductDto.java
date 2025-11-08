package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductDto
{
    private String name;
    private String prodDescription;
    private BigDecimal price;
    private Integer rewardPts;
    private Long categoryId;
    private String imageUrl;
}
