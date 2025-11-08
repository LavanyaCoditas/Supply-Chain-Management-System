package com.project.supply.chain.management.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "central_office_product_request")
@Data @NoArgsConstructor @AllArgsConstructor
public class CentralOfficeProductRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "central_office_id")
    private CentralOffice centralOffice;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne @JoinColumn(name = "factory_id")
    private Factory factory;

    private Integer qtyRequested;
}

