package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactoryProductStockResponseDto {
    private Long productId;
    private String productName;
    private String categoryName;
    private BigDecimal price;
    private Long threshold;
    private Integer currentQty;
    private String imageUrl;
    private Integer rewardPts;
}
