package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FactoryService {
    ApiResponseDto<Void> createFactory(FactoryDto factoryDto);
    ApiResponseDto<Void> createEmployeeAsPlantHead(AddEmployeeDto addEmployeeDto);
    ApiResponseDto<Page<FactoryDto>> getAllFactories(String search, Pageable pageable);

    ApiResponseDto<Void> updateFactory(Long factoryId, FactoryDto updateFactoryDto);
    ApiResponseDto<List<FactoryProductionSummaryDto>> getFactoryProductionSummary();
    ApiResponseDto<FactoryDetailsDto> getFactoryDetails(Long factoryId);

    ApiResponseDto<Void> deleteFactory(Long factoryId);
}
