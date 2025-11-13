package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.constants.ToolType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetToolDto {
    private Long id;
    private String name;
    private String categoryName;
    private ToolType type;
    private Expensive isExpensive;
    private Integer threshold;
    private Long qty;
    private String imageUrl;
}
