package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchandiseResponseDto {
    private Long id;
    private String name;
    private Integer requiredPoints;
    private Integer availableQuantity;
    private String imageUrl;

}
