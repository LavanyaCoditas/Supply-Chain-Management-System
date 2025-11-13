package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.Tool;
import com.project.supply.chain.management.entity.ToolStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ToolStockRepository extends JpaRepository<ToolStock,Long> {
    Optional<ToolStock> findByToolAndFactory(Tool tool, Factory factory);
    List<ToolStock> findByFactory(Factory factory);
}
