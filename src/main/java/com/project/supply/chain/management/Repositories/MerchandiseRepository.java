package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Merchandise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Long> {
    // Additional custom queries if needed
}
