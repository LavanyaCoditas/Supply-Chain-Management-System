package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.DistributorInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributorInventoryRepository extends JpaRepository<DistributorInventory, Long> {
}
