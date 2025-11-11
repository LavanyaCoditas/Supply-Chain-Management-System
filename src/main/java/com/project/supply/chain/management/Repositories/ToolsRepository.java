package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Tool;
import com.project.supply.chain.management.entity.ToolCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ToolsRepository extends JpaRepository<Tool,Long> {
    List<Tool> findByCategory(ToolCategory category);

    boolean existsByNameIgnoreCase(String name);
    Optional<Tool> findByNameIgnoreCase(String name);

}
