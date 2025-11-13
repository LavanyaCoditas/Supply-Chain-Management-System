package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import org.springframework.data.domain.Page;

public interface CheifSupervisorService {

    ApiResponseDto<WorkerResponseDto> addWorker(AddEmployeeDto dto);
    ApiResponseDto<WorkerResponseDto> updateWorker(Long workerId, UpdateEmployeeDto dto);
    ApiResponseDto<Void> softDeleteWorker(Long workerId);
    ApiResponseDto<Page<WorkerResponseDto>> searchWorkers(
            String name,
            String factoryName,
            String bayName,
            int page,
            int size
    );
}
