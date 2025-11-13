package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tool_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🧰 Each request can have multiple tool items
    @OneToMany(mappedBy = "toolRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToolRequestItem> toolItems;

    // 👷 The worker who made the request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    // ✅ No need for a single requestQty — each ToolRequestItem has its own quantity
    // private Integer requestQty; ❌ remove this

    // 👨‍🏭 The person (Plant Head / Chief Supervisor) who approved/rejected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    private ToolOrProductRequestStatus status = ToolOrProductRequestStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
