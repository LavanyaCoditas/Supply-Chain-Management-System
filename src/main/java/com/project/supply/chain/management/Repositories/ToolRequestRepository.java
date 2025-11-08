package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.ToolRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRequestRepository extends JpaRepository<ToolRequest, Long> {
    // Additional custom queries if needed
}
