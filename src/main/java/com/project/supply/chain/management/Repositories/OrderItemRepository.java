package com.project.supply.chain.management.Repositories;
//package com.project.supply.chain.management.repository;

import com.project.supply.chain.management.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Additional custom queries if needed
}
