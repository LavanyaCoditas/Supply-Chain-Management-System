package com.project.supply.chain.management.dto;

import lombok.Data;

@Data
public class WorkerReturnRequestDto {
    private Long issuanceId;
    private Integer quantity;
}
