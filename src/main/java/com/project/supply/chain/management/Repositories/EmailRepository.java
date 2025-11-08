package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email,Long> {
}
