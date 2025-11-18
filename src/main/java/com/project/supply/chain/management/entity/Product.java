package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.Account_Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private ProductCategory category;

    @Column(unique = true)
    private String name;

    @Column(columnDefinition = "text")
    private String image;

    @Column

    private String prodDescription;

    @Column
    @Positive(message = "price must be postive")
    private BigDecimal price;

    @Column
    @Positive(message = "reward points must be positive")
    private Integer rewardPts;

    @Column(name = "threshold")
    @Positive(message = "threshold must be positive")
    private Long threshold;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Account_Status isActive;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}

