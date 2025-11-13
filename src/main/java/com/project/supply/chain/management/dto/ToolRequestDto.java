package com.project.supply.chain.management.dto;

import lombok.Data;

import java.util.List;

@Data
public class ToolRequestDto {

        private List<Long> toolIds;
        private List<Integer> quantities;


}
