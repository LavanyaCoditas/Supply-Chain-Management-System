package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "distributor_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributorOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long distributorId;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column
    @Size(min = 1, max = 100, message = "rejection reason must be between 1-100 characters")
    private String rejectReason;

    @Column
    private Long invoiceId;
}

