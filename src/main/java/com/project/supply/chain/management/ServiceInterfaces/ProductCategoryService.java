package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.ProductCategoryDto;
import com.project.supply.chain.management.dto.ProductCategoryResponseDto;

import java.util.List;

public interface ProductCategoryService
{
    ApiResponseDto<Void> createProductCategory(ProductCategoryDto productCategoryDto);
    ApiResponseDto<Void> updateProductCategory(Long categoryId, ProductCategoryDto productCategoryDto);
    ApiResponseDto<List<ProductCategoryResponseDto>> getAllCategories(String sortBy, String sortDir);
    ApiResponseDto<Void> deleteProductCategory(Long categoryId);

}
