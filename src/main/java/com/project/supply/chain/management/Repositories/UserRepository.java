package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User> {
    @Override
    Optional<User> findById(Long aLong);

    User findByEmail(String email);
    @Query("""
        SELECT DISTINCT u
        FROM User u
        JOIN u.factoryMappings fm
        WHERE fm.factory.id = :factoryId
        """)
    List<User> findAllByFactory(Long factoryId);

    //active inactive employees
    @Query("""
        SELECT COUNT(u)
        FROM User u
        JOIN u.factoryMappings fm
        WHERE fm.factory.id = :factoryId AND u.isActive = :status
        """)
    int countByFactoryAndStatus(Long factoryId, Account_Status status);
    boolean existsByEmail(String email);

    boolean existsByUsername( String username);
}
