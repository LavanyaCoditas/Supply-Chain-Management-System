package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.ToolReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolReturnRepository extends JpaRepository<ToolReturn, Long> {

}
