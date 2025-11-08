package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Factory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FactoryRepository extends JpaRepository<Factory,Long>, JpaSpecificationExecutor<Factory> {
}
