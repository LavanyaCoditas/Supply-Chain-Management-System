package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddCentralOfficerDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.CentralOfficeDto;
import com.project.supply.chain.management.dto.CentralOfficeResponseDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CentralOfficeService {


    ApiResponse<Void> createCentralOffice(CentralOfficeDto dto);

    @Transactional
    ApiResponse<Void> addCentralOfficerToOffice(AddCentralOfficerDto dto);
    ApiResponse<List<CentralOfficeResponseDto>> getCentralOffice();

}