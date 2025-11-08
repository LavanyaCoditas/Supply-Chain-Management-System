package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.UserCentralOfficeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCentralOfficeRepository extends JpaRepository<UserCentralOfficeMapping,Long> {
}
