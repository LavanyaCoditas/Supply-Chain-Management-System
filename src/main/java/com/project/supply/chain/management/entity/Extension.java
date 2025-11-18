package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ExtensionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "extensions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Extension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private User worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @Enumerated(EnumType.STRING)
    @Column
    private ExtensionStatus status = ExtensionStatus.APPROVED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(columnDefinition = "text")
    @Size(min=1,max = 100,message = "comment must be between 1-100 characters")
    private String comment;
}
