package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
        import lombok.*;

@Entity
@Table(name = "factories_inventory_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactoriesInventoryStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockEntryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column
    private Integer qty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;
}
