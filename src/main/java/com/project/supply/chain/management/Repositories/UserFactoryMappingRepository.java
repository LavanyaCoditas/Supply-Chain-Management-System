package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFactoryMappingRepository extends JpaRepository<UserFactoryMapping,Long> {
    Optional<UserFactoryMapping> findByUser(User user);
}
