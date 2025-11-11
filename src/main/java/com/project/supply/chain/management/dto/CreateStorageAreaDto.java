package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStorageAreaDto {
    private Integer rowNum;
    private Integer colNum;
    private Integer stack;
}
