package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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


    @OneToMany(mappedBy = "toolRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToolRequestItem> toolItems;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column
    @Size(min = 10, max=200, message = "descirption must be between 1 to 200 words")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    private ToolOrProductRequestStatus status = ToolOrProductRequestStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();


}
