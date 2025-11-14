package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FactoryRepository extends JpaRepository<Factory,Long>, JpaSpecificationExecutor<Factory> {
    Optional<Factory> findByName(String factoryName);


}
