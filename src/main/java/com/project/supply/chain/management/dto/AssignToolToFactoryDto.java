package com.project.supply.chain.management.dto;

import lombok.Data;

@Data
public class AssignToolToFactoryDto {
    private Long toolId;
    private Long storageAreaId;
    private Integer quantity;
}
