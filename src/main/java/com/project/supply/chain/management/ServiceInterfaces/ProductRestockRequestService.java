package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductRestockRequestService {

    public ApiResponseDto<ProductRestockRequestDto> createRestockRequest(CreateRestockRequestDto requestDto);
    public ApiResponseDto<Page<ProductRestockRequestDto>> getMyRestockRequests(ToolOrProductRequestStatus status, BaseRequestDto requestDto);
    public ApiResponseDto<String> updateStockDirectly(UpdateProductStockDto stockDto);
    public ApiResponseDto<ProductRestockRequestDto> completeRestockRequest(Long requestId);
    public ApiResponseDto<Page<ProductRestockRequestDto>> getMyFactoryRestockRequests(
            ToolOrProductRequestStatus status, BaseRequestDto requestDto);
    public ApiResponseDto<Page<CentralOfficeInventoryDto>> getCentralOfficeInventory(
            Long productId, String productName, Long minQuantity, Long maxQuantity, BaseRequestDto requestDto) ;

    }
