package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "central_office_id")
    private CentralOffice centralOffice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne
    @JoinColumn(name = "requested_by_user_id")
    private User requestedByUser;

    private Integer qtyRequested;

    @Enumerated(EnumType.STRING)
    private ToolOrProductRequestStatus status;

    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime completedAt;
}
