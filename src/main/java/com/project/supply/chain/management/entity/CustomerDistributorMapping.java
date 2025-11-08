package com.project.supply.chain.management.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "customer_distributor_mapping")
@Data @NoArgsConstructor @AllArgsConstructor
public class CustomerDistributorMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne

    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne

    @JoinColumn(name = "distributor_id")
    private User distributor;

    private Instant assignedAt = Instant.now();
}


