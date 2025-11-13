package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddToolCategoryDto;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.ToolCategoryDto;

import java.util.List;

public interface ToolCategoryService {
    ApiResponseDto<ToolCategoryDto> addToolCategory(AddToolCategoryDto dto);
    ApiResponseDto<List<ToolCategoryDto>> getAllToolCategories();

    ApiResponseDto<ToolCategoryDto> updateToolCategory(Long id, AddToolCategoryDto dto);
    ApiResponseDto<Void> deleteToolCategory(Long id);

}
