package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User> {
    User findByEmail(String email);
}
