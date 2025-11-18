package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.CentralOfficeInventory;
import com.project.supply.chain.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CentralOfficeInventoryRepository extends JpaRepository<CentralOfficeInventory,Long>
 ,JpaSpecificationExecutor<CentralOfficeInventory>

    {
        Optional<CentralOfficeInventory> findByProduct(Product product);
        Optional<CentralOfficeInventory> findByProductId(Long productId);
        boolean existsByProduct(Product product);
}
