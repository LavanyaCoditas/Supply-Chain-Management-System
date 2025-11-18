package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

    @Entity
    @Table(name = "invoice")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Invoice {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id")
        private DistributorOrder order;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id")
        private User customer;

        @Column(nullable = false)
        private Long distributorId;

        @Column(columnDefinition = "text")
        private String csvFileUrl;

        @Column(columnDefinition = "text")
        private String pdfUrl;

        @Column(nullable = false)
        private LocalDate date;

        @Column(nullable = false)
        private BigDecimal totalAmount;
    }


