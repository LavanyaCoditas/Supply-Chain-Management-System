package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.BayRequestDto;
import com.project.supply.chain.management.dto.EmployeeRequestDto;

public interface PlantHeadService {

        ApiResponse<String> createBay(Long plantHeadId, BayRequestDto request);
//        String createEmployee(String plantHeadEmail, EmployeeRequestDto request);

}
