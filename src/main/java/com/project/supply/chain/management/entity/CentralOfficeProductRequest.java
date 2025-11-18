package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "central_office_product_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentralOfficeProductRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "central_office_id")
    private CentralOffice centralOffice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id")
    private User requestedByUser;

    @Column(nullable = false)
    @Positive(message = "Quantity must be positive")
    private Integer qtyRequested;

    @Enumerated(EnumType.STRING)
    @Column
    private ToolOrProductRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column
    private LocalDateTime completedAt;
}
