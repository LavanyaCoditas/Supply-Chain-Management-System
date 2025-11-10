package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddMerchandiseDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.MerchandiseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MerchandiseService {
    ApiResponse<MerchandiseResponseDto> addMerchandise(AddMerchandiseDto dto, MultipartFile image) throws IOException;
    ApiResponse<Page<MerchandiseResponseDto>> getAllMerchandise(int page, int size, String search, String sort);
    ApiResponse<Void> softDeleteMerchandise(Long id);
    ApiResponse<MerchandiseResponseDto> restockMerchandise(Long id, Long additionalQuantity);

    ApiResponse<MerchandiseResponseDto> updateMerchandise(Long id, AddMerchandiseDto dto, MultipartFile imageFile) throws Exception;

}
