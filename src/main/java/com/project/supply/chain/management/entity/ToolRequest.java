package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.persistence.*;
        import lombok.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "tool_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tool_id")
    private Tool tool;
   
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    private Integer requestQty;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    private ToolOrProductRequestStatus status = ToolOrProductRequestStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
