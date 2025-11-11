package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.*;

import java.io.IOException;
import java.util.List;

public interface ToolService {

    ApiResponse<String> createStorageArea(CreateStorageAreaDto dto);
    ApiResponse<List<StorageAreaResponseDto>> getAllStorageAreasForPlantHead();

    ApiResponse<ToolResponseDto> createTool(AddNewToolDto dto) throws IOException;
    ApiResponse<ToolResponseDto> assignToolToFactory(AssignToolToFactoryDto dto);
}
