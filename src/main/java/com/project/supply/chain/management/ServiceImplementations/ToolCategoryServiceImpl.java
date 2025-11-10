package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.ToolCategoryRepository;
import com.project.supply.chain.management.Repositories.ToolsRepository;
import com.project.supply.chain.management.ServiceInterfaces.ToolCategoryService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.dto.AddToolCategoryDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ToolCategoryDto;
import com.project.supply.chain.management.entity.Tool;
import com.project.supply.chain.management.entity.ToolCategory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolCategoryServiceImpl implements ToolCategoryService {
    @Autowired
    ToolCategoryRepository toolCategoryRepository;
    @Autowired
    ToolsRepository toolsRepository;
    @Override
    public ApiResponse<ToolCategoryDto> addToolCategory(AddToolCategoryDto dto) {
        // Check for duplicate
        if (toolCategoryRepository.existsByNameIgnoreCase(dto.getName())) {
            return new ApiResponse<>(false, "Tool category with this name already exists", null);
        }

        ToolCategory category = new ToolCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        ToolCategory saved = toolCategoryRepository.save(category);

        ToolCategoryDto response = new ToolCategoryDto(
                saved.getId(),
                saved.getName(),
                saved.getDescription()
        );

        return new ApiResponse<>(true, "Tool category created successfully", response);
    }


    @Override
    public ApiResponse<List<ToolCategoryDto>> getAllToolCategories() {
        List<ToolCategory> categories = toolCategoryRepository.findAll();

        if (categories.isEmpty()) {
            return new ApiResponse<>(false, "No tool categories found", null);
        }

        List<ToolCategoryDto> dtoList = categories.stream()
                .map(cat -> new ToolCategoryDto(
                        cat.getId(),
                        cat.getName(),
                        cat.getDescription()
                ))
                .toList();

        return new ApiResponse<>(true, "Tool categories fetched successfully", dtoList);
    }
    @Override
    public ApiResponse<ToolCategoryDto> updateToolCategory(Long id, AddToolCategoryDto dto) {
        // ✅ 1. Fetch existing category
        ToolCategory category = toolCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tool category not found"));

        // ✅ 2. Check duplicate name if changing
        if (dto.getName() != null &&
                !dto.getName().equalsIgnoreCase(category.getName()) &&
                toolCategoryRepository.existsByNameIgnoreCase(dto.getName())) {
            return new ApiResponse<>(false, "Tool category with this name already exists", null);
        }

        // ✅ 3. Update fields
        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());

        ToolCategory updated = toolCategoryRepository.save(category);

        // ✅ 4. Build response
        ToolCategoryDto response = new ToolCategoryDto(
                updated.getId(),
                updated.getName(),
                updated.getDescription()
        );

        return new ApiResponse<>(true, "Tool category updated successfully", response);
    }


    //delte tool category and the tools under it
    @Override
    @Transactional
    public ApiResponse<Void> deleteToolCategory(Long id) {
        // 1️⃣ Find tool category
        ToolCategory category = toolCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tool category not found"));

        // 2️⃣ Get all tools under this category
        List<Tool> tools = toolsRepository.findByCategory(category);

        // 3️⃣ Mark each tool as INACTIVE (soft delete)
        for (Tool tool : tools) {
            tool.setIsActive(Account_Status.IN_ACTIVE);
        }
        toolsRepository.saveAll(tools);

        // 4️⃣ Delete category
        toolCategoryRepository.delete(category);

        // 5️⃣ Return success
        return new ApiResponse<>(true, "Tool category deleted successfully and all related tools set to inactive", null);
    }

}
