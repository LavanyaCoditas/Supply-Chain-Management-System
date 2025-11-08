package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.Bay;
import com.project.supply.chain.management.entity.Factory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BayRepository extends JpaRepository<Bay, Long> {
    // Additional custom queries if needed
    List<Bay> findByFactory(Factory factory);


}
