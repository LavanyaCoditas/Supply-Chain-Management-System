package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.ToolStorageMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolStorageMappingRepository extends JpaRepository<ToolStorageMapping, Long> {
    // Additional custom queries if needed
}
