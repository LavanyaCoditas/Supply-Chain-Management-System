package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.Factory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PlantHeadService {

        ApiResponse<String> createBay(Long plantHeadId, BayRequestDto request);

        ApiResponse<List<BayListdto>> getBaysInFactory();
        ApiResponse<UserResponseDto> createEmployeeForCurrentPlantHead(EmployeeRequestDto request);
//        String createEmployee(String plantHeadEmail, EmployeeRequestDto request);
ApiResponse<Page<UserResponseDto>> getEmployeesInFactory(
        String keyword, String role, int page, int size
);

        ApiResponse<Void> updateFactoryProductStock(UpdateStockRequestDto request);
        ApiResponse<List<FactoryProductStockResponseDto>> getAllProductsWithStock();
        ApiResponse<List<FactoryProductStockResponseDto>> getLowStockProducts();
}
