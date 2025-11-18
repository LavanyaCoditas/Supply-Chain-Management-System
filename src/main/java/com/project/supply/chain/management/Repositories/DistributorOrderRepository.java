package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.DistributorOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributorOrderRepository extends JpaRepository<DistributorOrder, Long> {
}
