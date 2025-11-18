package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tool_restock_requests")
@Data @NoArgsConstructor @AllArgsConstructor
public class ToolRestockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restocked_by")
    private User restockedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @Positive(message = "tool quantity entered must be positive")
    private Integer toolQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @Enumerated(EnumType.STRING)
    private ToolOrProductRequestStatus status = ToolOrProductRequestStatus.PENDING;

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
}
