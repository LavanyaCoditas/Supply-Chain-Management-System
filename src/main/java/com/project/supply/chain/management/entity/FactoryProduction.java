package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer producedQty;

    private LocalDateTime productionDate;
}

