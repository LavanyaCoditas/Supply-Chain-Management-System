package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.FactoriesInventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactoryInventoryStockRepository extends JpaRepository<FactoriesInventoryStock, Long> {
    // Additional custom queries if needed
}
