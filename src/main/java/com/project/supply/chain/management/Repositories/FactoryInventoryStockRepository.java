package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.FactoriesInventoryStock;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactoryInventoryStockRepository extends JpaRepository<FactoriesInventoryStock, Long> {
    // Additional custom queries if needed
    Optional<FactoriesInventoryStock> findByFactoryAndProduct(Factory factory, Product product);
    List<FactoriesInventoryStock> findAllByFactory(Factory factory);

    // ✅ Custom join query to fetch all products with stock in that factory
    @Query("""
        SELECT s FROM FactoriesInventoryStock s
        RIGHT JOIN Product p ON s.product = p
        WHERE s.factory = :factory OR s.factory IS NULL
        """)
    List<FactoriesInventoryStock> findAllProductsWithFactoryStock(Factory factory);

}
