package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.CreateRestockRequestDto;
import com.project.supply.chain.management.dto.ProductRestockRequestDto;
import com.project.supply.chain.management.dto.UpdateProductStockDto;

public interface ProductRestockRequestService {
//    public interface ProductRestockRequestService {
    ApiResponseDto<ProductRestockRequestDto> createRestockRequest(CreateRestockRequestDto dto);
//    ApiResponseDto<ProductRestockRequestDto> completeRestockRequest(Long requestId);
  //  ApiResponseDto<String> updateStockDirectly(UpdateProductStockDto stockDto);

}
