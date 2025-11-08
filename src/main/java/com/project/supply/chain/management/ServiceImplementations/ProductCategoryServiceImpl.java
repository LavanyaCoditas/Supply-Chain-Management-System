package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.ProductCategoryRepository;
import com.project.supply.chain.management.ServiceInterfaces.ProductCategoryService;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ProductCategoryDto;
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
    public ApiResponse<Void> createProductCategory(ProductCategoryDto productCategoryDto) {


        if (productCategoryDto.getCategoryName() == null || productCategoryDto.getCategoryName().trim().isEmpty()) {
            return new ApiResponse<>(false, "Category name cannot be empty", null);
        }

        boolean exists = categoryRepository.existsByCategoryNameIgnoreCase(productCategoryDto.getCategoryName());
        if (exists) {
            return new ApiResponse<>(false, "Category with this name already exists", null);
        }

        ProductCategory category = new ProductCategory();
        category.setCategoryName(productCategoryDto.getCategoryName().trim());
        category.setDescription(productCategoryDto.getDescription());
        categoryRepository.save(category);

        return new ApiResponse<>(true, "Product category created successfully", null);
    }
    @Override
    public ApiResponse<Void> updateProductCategory(Long categoryId, ProductCategoryDto dto) {
        ProductCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        if (!existingCategory.getCategoryName().equalsIgnoreCase(dto.getCategoryName()) &&
                categoryRepository.existsByCategoryNameIgnoreCase(dto.getCategoryName())) {
            return new ApiResponse<>(false, "Another category with this name already exists", null);
        }

        existingCategory.setCategoryName(dto.getCategoryName());
        existingCategory.setDescription(dto.getDescription());
        categoryRepository.save(existingCategory);

        return new ApiResponse<>(true, "Category updated successfully", null);
    }
    @Override
    public ApiResponse<List<ProductCategory>> getAllCategories(String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        List<ProductCategory> categories = categoryRepository.findAll(sort);
        return new ApiResponse<>(true, "Categories fetched successfully", categories);
    }

    @Override
    public ApiResponse<Void> deleteProductCategory(Long categoryId) {
        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            return new ApiResponse<>(false, "Product category not found with ID: " + categoryId, null);
        }


        categoryRepository.deleteById(categoryId);

        return new ApiResponse<>(true, "Product category deleted successfully", null);
    }
}
