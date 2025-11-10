package com.project.supply.chain.management.ServiceInterfaces;

import com.cloudinary.Api;
import com.project.supply.chain.management.dto.AddEmployeeDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.FactoryDto;
import com.project.supply.chain.management.dto.FactoryProductionSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FactoryService {
    ApiResponse<Void> createFactory(FactoryDto factoryDto);
    ApiResponse<Void> createEmployeeAsPlantHead(AddEmployeeDto addEmployeeDto);
    ApiResponse<Page<FactoryDto>> getAllFactories(String search, Pageable pageable);

    ApiResponse<Void> updateFactory(Long factoryId, FactoryDto updateFactoryDto);
    ApiResponse<List<FactoryProductionSummaryDto>> getFactoryProductionSummary();

    ApiResponse<Void> deleteFactory(Long factoryId);
}
