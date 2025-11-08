package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.StorageArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageAreaRepository extends JpaRepository<StorageArea, Long> {
    // Additional custom queries if needed
}
