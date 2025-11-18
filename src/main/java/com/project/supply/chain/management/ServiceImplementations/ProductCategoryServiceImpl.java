package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.ProductCategoryRepository;
import com.project.supply.chain.management.ServiceInterfaces.ProductCategoryService;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.ProductCategoryDto;
import com.project.supply.chain.management.dto.ProductCategoryResponseDto;
import com.project.supply.chain.management.entity.ProductCategory;
import com.project.supply.chain.management.exceptions.ResourceAlreadyExistsException;
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    public ApiResponseDto<Void> createProductCategory(ProductCategoryDto productCategoryDto) {


        if (productCategoryDto.getCategoryName() == null || productCategoryDto.getCategoryName().trim().isEmpty()) {
            throw  new IllegalArgumentException( "Category name cannot be empty");
        }

        boolean exists = categoryRepository.existsByCategoryNameIgnoreCase(productCategoryDto.getCategoryName());
        if (exists) {
            throw  new ResourceAlreadyExistsException( "Category with this name already exists");
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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        if (!existingCategory.getCategoryName().equalsIgnoreCase(dto.getCategoryName()) &&
                categoryRepository.existsByCategoryNameIgnoreCase(dto.getCategoryName())) {
            throw  new ResourceAlreadyExistsException( "Another category with this name already exists");
        }

        existingCategory.setCategoryName(dto.getCategoryName());
        existingCategory.setDescription(dto.getDescription());
        categoryRepository.save(existingCategory);

        return new ApiResponseDto<>(true, "Category updated successfully", null);
    }
    @Override
    public ApiResponseDto<List<ProductCategoryResponseDto>> getAllCategories(String sortBy, String sortDir) {
        // sort direction
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        //  Fetch categories
        List<ProductCategory> categories = categoryRepository.findAll(sort);


        List<ProductCategoryResponseDto> categoryDtos = categories.stream()
                .map(category -> new ProductCategoryResponseDto(
                        category.getId(),
                        category.getCategoryName(),
                        category.getDescription(),
                        category.getProducts() != null ? category.getProducts().size() : 0
                ))
                .toList();

        //  Return clean response
        return new ApiResponseDto<>(true, "Categories fetched successfully", categoryDtos);
    }


    @Override
    public ApiResponseDto<Void> deleteProductCategory(Long categoryId) {
        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw  new ResourceNotFoundException("Product category not found with ID: " + categoryId);
        }

        categoryRepository.deleteById(categoryId);

        return new ApiResponseDto<>(true, "Product category deleted successfully", null);
    }
}
