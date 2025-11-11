package com.project.supply.chain.management.Repositories;


import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.StorageArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageAreaRepository extends JpaRepository<StorageArea, Long> {
    // Additional custom queries if needed
    List<StorageArea> findByFactory(Factory factory);
    boolean existsByFactoryAndRowNumAndColNumAndStack(
            Factory factory,
            Integer rowNum,
            Integer colNum,
            Integer stack
    );
    boolean existsByFactoryAndRowNum(Factory factory, Integer row);
    boolean existsByFactoryAndRowNumAndColNum(Factory factory, Integer row, Integer col);
}
