
package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.constants.ToolType;
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
    private ToolType type;
    private Expensive isExpensive;
    private Integer threshold;
    private String image;
}
