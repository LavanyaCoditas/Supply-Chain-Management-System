package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddProductDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ProductResponseDto;
import com.project.supply.chain.management.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ApiResponse<ProductResponseDto> uploadProductWithImage(AddProductDto productDto, MultipartFile imageFile);
    ApiResponse<Page<ProductResponseDto>> getAllProducts(int page, int size, String search, String categoryName);
    ApiResponse<String> softDeleteProduct(Long productId);

}
