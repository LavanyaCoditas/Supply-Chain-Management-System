// 📁 com.project.supply.chain.management.dto.ToolResponseDto.java
package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Expensive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolResponseDto {
    private Long id;
    private String name;
    private String categoryName;
    private String type;
    private Expensive isExpensive;
    private Integer threshold;
    private Integer quantity;
    private String storageAreaName;
    private String factoryName;
    private String image;
}
