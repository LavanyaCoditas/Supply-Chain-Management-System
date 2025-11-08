package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "distributor_inventory")
@Data @NoArgsConstructor @AllArgsConstructor
public class DistributorInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long distributorId;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    private Integer stockQty;
}

