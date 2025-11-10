package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddToolCategoryDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ToolCategoryDto;

import java.util.List;

public interface ToolCategoryService {
    ApiResponse<ToolCategoryDto> addToolCategory(AddToolCategoryDto dto);
    ApiResponse<List<ToolCategoryDto>> getAllToolCategories();

    ApiResponse<ToolCategoryDto> updateToolCategory(Long id, AddToolCategoryDto dto);
    ApiResponse<Void> deleteToolCategory(Long id);

}
