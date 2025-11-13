package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlantHeadService {

        ApiResponseDto<String> createBay( BayRequestDto request);

        ApiResponseDto<List<BayListdto>> getBaysInFactory();
        ApiResponseDto<UserResponseDto> createEmployeeForCurrentPlantHead(EmployeeRequestDto request);
//        String createEmployee(String plantHeadEmail, EmployeeRequestDto request);
ApiResponseDto<Page<UserResponseDto>> getEmployeesInFactory(
        String keyword, String role, int page, int size
);

        ApiResponseDto<Void> updateFactoryProductStock(UpdateStockRequestDto request);
        ApiResponseDto<List<FactoryProductStockResponseDto>> getAllProductsWithStock();
        ApiResponseDto<List<FactoryProductStockResponseDto>> getLowStockProducts();
}
