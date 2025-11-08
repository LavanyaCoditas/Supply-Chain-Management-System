package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFactoryMappingRepository extends JpaRepository<UserFactoryMapping,Long>, JpaSpecificationExecutor<UserFactoryMapping> {
    Optional<UserFactoryMapping> findByUser(User user);
    // Find mapping by user (used for plant head)


    // Check if a Chief Supervisor already exists for a factory
    boolean existsByFactoryAndAssignedRole(Factory factory, Role assignedRole);
}
