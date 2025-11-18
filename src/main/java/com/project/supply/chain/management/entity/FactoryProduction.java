package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "factory_production")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactoryProduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    @Positive(message = "produced quantity must be positive")
    private Integer producedQty;

    @Column(nullable = false)
    private LocalDateTime productionDate;
}

