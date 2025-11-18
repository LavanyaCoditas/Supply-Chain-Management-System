package com.project.supply.chain.management.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    @Column
    @Positive(message = "price must be positive")
    private BigDecimal pricePerUnit;
}
