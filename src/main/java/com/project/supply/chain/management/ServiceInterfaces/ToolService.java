package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ToolService {

    ApiResponseDto<ToolResponseDto> createTool(ToolDto dto) throws IOException;
    ApiResponseDto<ToolResponseDto> updateToolImage(Long toolId, MultipartFile image) throws IOException;
    ApiResponseDto<ToolResponseDto> updateTool(Long toolId, ToolDto dto);
    ApiResponseDto<String> addToolToFactoryStock(ToolInventoryStockDto dto);
    ApiResponseDto<List<GetToolDto>> getAllToolsForOwner(
            String searchName,
            String categoryName,
            String type,
            int page,
            int size,
            String sortBy,
            String sortDir);

}
