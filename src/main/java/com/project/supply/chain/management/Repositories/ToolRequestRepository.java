package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.ToolRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRequestRepository extends JpaRepository<ToolRequest, Long>, JpaSpecificationExecutor<ToolRequest> {

    Page<ToolRequest> findAll(Specification<ToolRequest> spec, Pageable pageable);
    // Additional custom queries if needed
}
