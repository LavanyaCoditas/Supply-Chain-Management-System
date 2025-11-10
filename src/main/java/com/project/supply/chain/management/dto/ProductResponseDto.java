package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Account_Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String prodDescription;
    private BigDecimal price;
    private Integer rewardPts;
    private String categoryName;
    private Long threshold;
    private String imageUrl;
    private Account_Status isActive;
}
