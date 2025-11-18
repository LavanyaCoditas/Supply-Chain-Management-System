package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.Tool;
import com.project.supply.chain.management.entity.ToolStorageMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolStorageMappingRepository extends JpaRepository<ToolStorageMapping, Long> {

    Optional<ToolStorageMapping> findByFactoryAndTool(Factory factory, Tool tool);
    Optional<ToolStorageMapping> findByToolAndFactory(Tool tool, Factory factory);

}
