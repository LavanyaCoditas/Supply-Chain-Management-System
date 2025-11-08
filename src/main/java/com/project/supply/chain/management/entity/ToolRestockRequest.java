package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tool_restock_requests")
@Data @NoArgsConstructor @AllArgsConstructor
public class ToolRestockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "restocked_by")
    private User restockedBy;

    @ManyToOne @JoinColumn(name = "tool_id")
    private Tool tool;

    private Integer toolQty;

    @ManyToOne @JoinColumn(name = "factory_id")
    private Factory factory;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ToolOrProductRequestStatus status = ToolOrProductRequestStatus.PENDING;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
