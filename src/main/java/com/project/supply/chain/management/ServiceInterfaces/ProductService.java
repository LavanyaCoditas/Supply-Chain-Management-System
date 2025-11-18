package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.dto.AddProductDto;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.ProductResponseDto;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ApiResponseDto<ProductResponseDto> uploadProductWithImage(AddProductDto productDto, MultipartFile imageFile) throws FileUploadException;
    ApiResponseDto<Page<ProductResponseDto>> getAllProducts(int page, int size, String search, String categoryName);
    ApiResponseDto<String> softDeleteProduct(Long productId);
    ApiResponseDto<ProductResponseDto> updateProduct(Long id, AddProductDto productDto, MultipartFile imageFile) throws Exception;

}
