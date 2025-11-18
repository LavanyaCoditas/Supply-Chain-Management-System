package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "tool_returns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_issuance_id")
    private ToolIssuance toolIssuance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column
    @Positive(message = "fit quantity of the tool must be positive")
    private Integer fitQty;
    @Column
    @Positive(message = "unfit quantity must be zero or positive")
    private Integer unfitQty;

    @Column
    @Positive(message = "Extended quantity must be zero or positive")
    private Integer extendedQty;
}
