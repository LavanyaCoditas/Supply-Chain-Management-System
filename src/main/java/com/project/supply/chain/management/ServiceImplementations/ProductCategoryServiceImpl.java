package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.ProductCategoryRepository;
import com.project.supply.chain.management.ServiceInterfaces.ProductCategoryService;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.ProductCategoryDto;
import com.project.supply.chain.management.dto.ProductCategoryResponseDto;
import com.project.supply.chain.management.entity.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ProductCategoryServiceImpl implements ProductCategoryService {
@Autowired
    private ProductCategoryRepository categoryRepository;

    @Override
    public ApiResponseDto<Void> createProductCategory(ProductCategoryDto productCategoryDto) {


        if (productCategoryDto.getCategoryName() == null || productCategoryDto.getCategoryName().trim().isEmpty()) {
            return new ApiResponseDto<>(false, "Category name cannot be empty", null);
        }

        boolean exists = categoryRepository.existsByCategoryNameIgnoreCase(productCategoryDto.getCategoryName());
        if (exists) {
            return new ApiResponseDto<>(false, "Category with this name already exists", null);
        }

        ProductCategory category = new ProductCategory();
        category.setCategoryName(productCategoryDto.getCategoryName().trim());
        category.setDescription(productCategoryDto.getDescription());
        categoryRepository.save(category);

        return new ApiResponseDto<>(true, "Product category created successfully", null);
    }
    @Override
    public ApiResponseDto<Void> updateProductCategory(Long categoryId, ProductCategoryDto dto) {
        ProductCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        if (!existingCategory.getCategoryName().equalsIgnoreCase(dto.getCategoryName()) &&
                categoryRepository.existsByCategoryNameIgnoreCase(dto.getCategoryName())) {
            return new ApiResponseDto<>(false, "Another category with this name already exists", null);
        }

        existingCategory.setCategoryName(dto.getCategoryName());
        existingCategory.setDescription(dto.getDescription());
        categoryRepository.save(existingCategory);

        return new ApiResponseDto<>(true, "Category updated successfully", null);
    }
    @Override
    public ApiResponseDto<List<ProductCategoryResponseDto>> getAllCategories(String sortBy, String sortDir) {
        // ✅ Determine sort direction
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        // ✅ Fetch categories from DB
        List<ProductCategory> categories = categoryRepository.findAll(sort);

        // ✅ Convert entities → DTOs (no nested products)
        List<ProductCategoryResponseDto> categoryDtos = categories.stream()
                .map(category -> new ProductCategoryResponseDto(
                        category.getId(),
                        category.getCategoryName(),
                        category.getDescription(),
                        category.getProducts() != null ? category.getProducts().size() : 0
                ))
                .toList();

        // ✅ Return clean response
        return new ApiResponseDto<>(true, "Categories fetched successfully", categoryDtos);
    }


    @Override
    public ApiResponseDto<Void> deleteProductCategory(Long categoryId) {
        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            return new ApiResponseDto<>(false, "Product category not found with ID: " + categoryId, null);
        }


        categoryRepository.deleteById(categoryId);

        return new ApiResponseDto<>(true, "Product category deleted successfully", null);
    }
}
