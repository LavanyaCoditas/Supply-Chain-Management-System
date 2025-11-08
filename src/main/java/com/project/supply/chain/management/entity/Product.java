package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.Account_Status;
import jakarta.persistence.*;
        import lombok.*;
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

    @ManyToOne @JoinColumn(name = "category_id")
    @ToString.Exclude
    private ProductCategory category;

    private String name;

    @Column(columnDefinition = "text")
    private String image;

    @Column(columnDefinition = "text")
    private String prodDescription;

    private java.math.BigDecimal price;

    private Integer rewardPts;

    @Enumerated(EnumType.STRING)
    private Account_Status isActive;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

