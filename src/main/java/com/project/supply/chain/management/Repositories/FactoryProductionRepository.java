package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.dto.FactoryProductionSummaryDto;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.FactoryProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FactoryProductionRepository extends JpaRepository<FactoryProduction,Long> {
    @Query("""
    SELECT new com.project.supply.chain.management.dto.FactoryProductionSummaryDto(
        fp.factory.name,
        COUNT(DISTINCT fp.product.id),
        SUM(fp.producedQty)
    )
    FROM FactoryProduction fp
    GROUP BY fp.factory.name
    ORDER BY fp.factory.name
""")
    List<FactoryProductionSummaryDto> getFactoryProductionSummary();
    List<FactoryProduction> findByFactory(Factory factory);

}
