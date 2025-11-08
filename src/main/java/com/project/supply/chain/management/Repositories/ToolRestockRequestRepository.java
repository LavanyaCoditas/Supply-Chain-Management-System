package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.ToolRestockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRestockRequestRepository extends JpaRepository<ToolRestockRequest, Long> {
    // Additional custom queries if needed
}
