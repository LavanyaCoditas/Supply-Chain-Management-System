package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddCentralOfficerDto;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.CentralOfficeDto;
import com.project.supply.chain.management.dto.CentralOfficeResponseDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CentralOfficeService {


    ApiResponseDto<Void> createCentralOffice(CentralOfficeDto dto);

    @Transactional
    ApiResponseDto<Void> addCentralOfficerToOffice(AddCentralOfficerDto dto);
    ApiResponseDto<List<CentralOfficeResponseDto>> getCentralOffice();

}