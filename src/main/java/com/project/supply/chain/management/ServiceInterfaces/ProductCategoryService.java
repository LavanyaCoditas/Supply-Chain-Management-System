package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ProductCategoryDto;
import com.project.supply.chain.management.dto.ProductCategoryResponseDto;
import com.project.supply.chain.management.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryService
{
    ApiResponse<Void> createProductCategory(ProductCategoryDto productCategoryDto);
    ApiResponse<Void> updateProductCategory(Long categoryId, ProductCategoryDto productCategoryDto);
    ApiResponse<List<ProductCategoryResponseDto>> getAllCategories(String sortBy, String sortDir);
    ApiResponse<Void> deleteProductCategory(Long categoryId);

}
