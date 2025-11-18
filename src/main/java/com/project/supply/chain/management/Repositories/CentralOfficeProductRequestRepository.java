package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.entity.CentralOfficeProductRequest;
import com.project.supply.chain.management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CentralOfficeProductRequestRepository extends JpaRepository<CentralOfficeProductRequest,Long> {

    Page<CentralOfficeProductRequest> findByRequestedByUserId(Long chiefOfficerId, Pageable pageable);

    Page<CentralOfficeProductRequest> findByRequestedByUserIdAndStatus(Long chiefOfficerId, ToolOrProductRequestStatus status, Pageable pageable);
    Page<CentralOfficeProductRequest> findByStatus(ToolOrProductRequestStatus status, Pageable pageable);

    @Query("SELECT r FROM CentralOfficeProductRequest r WHERE r.factory.id = :factoryId")
    Page<CentralOfficeProductRequest> findByFactoryId(@Param("factoryId") Long factoryId, Pageable pageable);

    @Query("SELECT r FROM CentralOfficeProductRequest r WHERE r.factory.id = :factoryId AND r.status = :status")
    Page<CentralOfficeProductRequest> findByFactoryIdAndStatus(@Param("factoryId") Long factoryId,
                                                               @Param("status") ToolOrProductRequestStatus status,
                                                               Pageable pageable);

    Page<CentralOfficeProductRequest> findByRequestedByUser(User requestedByUser, Pageable pageable);
}
