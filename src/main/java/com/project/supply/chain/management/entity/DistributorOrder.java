package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.OrderStatus;
import jakarta.persistence.*;
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

    private Long distributorId;

    @ManyToOne @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private String rejectReason;
    private Long invoiceId;
}

