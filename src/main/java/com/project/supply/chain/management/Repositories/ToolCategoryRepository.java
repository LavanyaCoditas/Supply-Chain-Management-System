package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.ToolCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolCategoryRepository extends JpaRepository<ToolCategory, Long> {

    boolean existsByNameIgnoreCase(String name);
}
