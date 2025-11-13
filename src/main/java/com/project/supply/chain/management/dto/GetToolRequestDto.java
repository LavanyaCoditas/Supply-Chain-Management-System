package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetToolRequestDto {
    private Long requestId;
    private List<String> toolNames;
    private String workerName;
    private List<Integer> quantities;
    private ToolOrProductRequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
