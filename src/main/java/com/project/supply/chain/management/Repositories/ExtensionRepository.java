package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.Extension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtensionRepository extends JpaRepository<Extension, Long> {
    // Additional custom queries if needed
}
