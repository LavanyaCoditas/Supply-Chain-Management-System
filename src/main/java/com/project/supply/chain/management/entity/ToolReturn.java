package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
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

    @ManyToOne @JoinColumn(name = "tool_issuance_id")
    private ToolIssuance toolIssuance;

    @ManyToOne @JoinColumn(name = "updated_by")
    private User updatedBy;

    private Integer fitQty;
    private Integer unfitQty;
    private Integer extendedQty;
}
