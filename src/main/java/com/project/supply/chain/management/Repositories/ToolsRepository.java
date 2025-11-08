package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolsRepository extends JpaRepository<Tool,Long> {
}
