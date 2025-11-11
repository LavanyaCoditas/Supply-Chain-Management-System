package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageAreaResponseDto {
    private Long id;
    private String bucket;
    private Integer rowNum;
    private Integer colNum;
    private Integer stack;
    private String factoryName;
}
