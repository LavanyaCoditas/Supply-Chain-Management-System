
package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.constants.ToolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ToolDto {

    @NotBlank(message = "tool name not entered")
    private String name;
    @NotNull(message = "category Id required")
    private Long categoryId;
    @NotBlank(message = "tool type required")
    private ToolType type;      // PERISHABLE, NON_PERISHABLE
    @NotBlank(message = "Mention whether tool is expensive or not")
    private Expensive isExpensive;
    @NotNull(message = "threshold is required for tool")
    private Integer threshold;
}
