package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.ToolCategoryRepository;
import com.project.supply.chain.management.Repositories.ToolsRepository;
import com.project.supply.chain.management.ServiceInterfaces.ToolCategoryService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.dto.AddToolCategoryDto;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.ToolCategoryDto;
import com.project.supply.chain.management.entity.Tool;
import com.project.supply.chain.management.entity.ToolCategory;
import com.project.supply.chain.management.exceptions.ResourceAlreadyExistsException;
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ToolCategoryServiceImpl implements ToolCategoryService {

    private final ToolCategoryRepository toolCategoryRepository;

    private final ToolsRepository toolsRepository;
    @Override
    public ApiResponseDto<ToolCategoryDto> addToolCategory(AddToolCategoryDto dto) {

        if (toolCategoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw  new ResourceAlreadyExistsException( "Tool category with this name already exists");
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

        return new ApiResponseDto<>(true, "Tool category created successfully", response);
    }


    @Override
    public ApiResponseDto<List<ToolCategoryDto>> getAllToolCategories() {
        List<ToolCategory> categories = toolCategoryRepository.findAll();

        if (categories.isEmpty()) {
            throw  new ResourceNotFoundException( "No tool categories found");
        }

        List<ToolCategoryDto> dtoList = categories.stream()
                .map(cat -> new ToolCategoryDto(
                        cat.getId(),
                        cat.getName(),
                        cat.getDescription()
                ))
                .toList();

        return new ApiResponseDto<>(true, "Tool categories fetched successfully", dtoList);
    }
    @Override
    public ApiResponseDto<ToolCategoryDto> updateToolCategory(Long id, AddToolCategoryDto dto) {

        ToolCategory category = toolCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool category not found"));

        if (dto.getName() != null &&
                !dto.getName().equalsIgnoreCase(category.getName()) &&
                toolCategoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw  new ResourceAlreadyExistsException( "Tool category with this name already exists");
        }

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());

        ToolCategory updated = toolCategoryRepository.save(category);

        ToolCategoryDto response = new ToolCategoryDto(
                updated.getId(),
                updated.getName(),
                updated.getDescription()
        );

        return new ApiResponseDto<>(true, "Tool category updated successfully", response);
    }


    @Override
    @Transactional
    public ApiResponseDto<Void> deleteToolCategory(Long id) {

        ToolCategory category = toolCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Tool category not found"));
        List<Tool> tools = toolsRepository.findByCategory(category);
        for (Tool tool : tools) {
            tool.setIsActive(Account_Status.IN_ACTIVE);
        }
        toolsRepository.saveAll(tools);
        toolCategoryRepository.delete(category);
        return new ApiResponseDto<>(true, "Tool category deleted successfully and all related tools set to inactive", null);
    }

}
