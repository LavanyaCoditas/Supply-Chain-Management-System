package com.project.supply.chain.management.dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductStockDto {

    private Long productId;
    private Integer quantity;

}
