package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.ProductService;
import com.project.supply.chain.management.dto.AddProductDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ProductResponseDto;
import com.project.supply.chain.management.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
    @Autowired
    private ProductService productService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<ProductResponseDto>> uploadProduct(
            @ModelAttribute AddProductDto productDto,
            @RequestPart("image") MultipartFile imageFile) {

        ApiResponse<ProductResponseDto> response = productService.uploadProductWithImage(productDto, imageFile);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryName) {

        ApiResponse<Page<ProductResponseDto>> response = productService.getAllProducts(page, size, search, categoryName);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponse<String>> softDeleteProduct(@PathVariable Long productId) {
        ApiResponse<String> response = productService.softDeleteProduct(productId);
        return ResponseEntity.ok(response);
    }




}
