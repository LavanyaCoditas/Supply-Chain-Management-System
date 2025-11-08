package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.*;
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

        @ManyToOne @JoinColumn(name = "order_id")
        private DistributorOrder order;

        @ManyToOne @JoinColumn(name = "customer_id")
        private User customer;

        private Long distributorId;

        @Column(columnDefinition = "text")
        private String csvFileUrl;

        @Column(columnDefinition = "text")
        private String pdfUrl;

        private LocalDate date;
        private java.math.BigDecimal totalAmount;
    }


