package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ExtensionStatus;
import jakarta.persistence.*;
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

    @ManyToOne @JoinColumn(name = "worker_id")
    private User worker;

    @ManyToOne @JoinColumn(name = "tool_id")
    private Tool tool;

    @Enumerated(EnumType.STRING)
    private ExtensionStatus status = ExtensionStatus.APPROVED;

    @ManyToOne @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(columnDefinition = "text")
    private String comment;
}
