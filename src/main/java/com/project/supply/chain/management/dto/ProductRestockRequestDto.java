package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRestockRequestDto {

    private Long id;
    @NotNull(message = "factoryId required")
    private Long factoryId;
    private String factoryName;
    private Long productId;
    private String productName;
    @NotNull(message = "quantity must be entered")
    private Integer qtyRequested;
    private ToolOrProductRequestStatus status;
    private LocalDateTime requestedAt;
    private Long requestedByUserId;
    private String requestedByUserName;

    private LocalDateTime completedAt;
    private Integer currentFactoryStock;
    private Long centralOfficeStock;
}