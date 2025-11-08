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

    @ManyToOne @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    private Integer qty;

    @ManyToOne @JoinColumn(name = "added_by")
    private User addedBy;
}
