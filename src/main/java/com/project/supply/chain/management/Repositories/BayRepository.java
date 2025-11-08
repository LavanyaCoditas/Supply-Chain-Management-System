package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.Bay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BayRepository extends JpaRepository<Bay, Long> {
    // Additional custom queries if needed
}
