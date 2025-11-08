package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.CentralOffice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CentralOfficeRepository extends JpaRepository<CentralOffice,Long> {
    boolean existsByLocation(String location);
}
