package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "tool_request_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_request_id", nullable = false)
    private ToolRequest toolRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;

    @Positive
    @Column
    private Integer quantity;
}
