package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.ToolIssuance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolIssuanceRepository extends JpaRepository<ToolIssuance, Long> {

}
