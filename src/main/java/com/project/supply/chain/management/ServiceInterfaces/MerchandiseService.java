package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddMerchandiseDto;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.MerchandiseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MerchandiseService {
    ApiResponseDto<MerchandiseResponseDto> addMerchandise(AddMerchandiseDto dto, MultipartFile image) throws IOException;
    ApiResponseDto<Page<MerchandiseResponseDto>> getAllMerchandise(
            int page,
            int size,
            String search,
            Integer minRewardPoints,
            Integer maxRewardPoints,
            String stockStatus,
            String sort
    );
    ApiResponseDto<Void> softDeleteMerchandise(Long id);
    ApiResponseDto<MerchandiseResponseDto> restockMerchandise(Long id, Integer additionalQuantity);

    ApiResponseDto<MerchandiseResponseDto> updateMerchandise(Long id, AddMerchandiseDto dto, MultipartFile imageFile) throws Exception;

}
