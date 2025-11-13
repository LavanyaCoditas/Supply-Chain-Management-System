// 📁 com.project.supply.chain.management.dto.ToolDto.java
package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.constants.ToolType;
import lombok.Data;

@Data
public class ToolDto {
    //tool dto
    private String name;
    private Long categoryId;
    private ToolType type;      // e.g. PERISHABLE, NON_PERISHABLE
    private Expensive isExpensive;  // e.g. YES, NO
    private Integer threshold;
}
