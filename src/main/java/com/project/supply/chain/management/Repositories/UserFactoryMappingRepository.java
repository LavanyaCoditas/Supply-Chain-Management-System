package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.UserFactoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFactoryMappingRepository extends JpaRepository<UserFactoryMapping,Long> {
}
