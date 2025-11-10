package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import org.springframework.data.domain.Page;

public interface CheifSupervisorService {

    ApiResponse<WorkerResponseDto> addWorker(AddEmployeeDto dto);
    ApiResponse<WorkerResponseDto> updateWorker(Long workerId, UpdateEmployeeDto dto);
    ApiResponse<Void> softDeleteWorker(Long workerId);
    ApiResponse<Page<WorkerResponseDto>> searchWorkers(
            String name,
            String factoryName,
            String bayName,
            int page,
            int size
    );
}
