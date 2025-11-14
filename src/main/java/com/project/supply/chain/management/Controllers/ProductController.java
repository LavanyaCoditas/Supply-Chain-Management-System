package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.ProductRestockRequestService;
import com.project.supply.chain.management.ServiceInterfaces.ProductService;
import com.project.supply.chain.management.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
    @Autowired
    private ProductService productService;
    @Autowired
    ProductRestockRequestService productRestockRequestService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponseDto<ProductResponseDto>> uploadProduct(
            @ModelAttribute AddProductDto productDto,
            @RequestPart("image") MultipartFile imageFile) {

        ApiResponseDto<ProductResponseDto> response = productService.uploadProductWithImage(productDto, imageFile);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<ProductResponseDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryName) {

        ApiResponseDto<Page<ProductResponseDto>> response = productService.getAllProducts(page, size, search, categoryName);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponseDto<String>> softDeleteProduct(@PathVariable Long productId) {
        ApiResponseDto<String> response = productService.softDeleteProduct(productId);
        return ResponseEntity.ok(response);
    }
    //update product remaining
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE')")
    public ResponseEntity<ApiResponseDto<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @ModelAttribute AddProductDto productDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws Exception {

        ApiResponseDto<ProductResponseDto> response = productService.updateProduct(id, productDto, imageFile);
        return ResponseEntity.ok(response);
    }

    // Chief Officer creates restock request
    @PreAuthorize("hasAuthority('CENTRAL_OFFICE')")
    @PostMapping("/restock-request/create")
    public ResponseEntity<ApiResponseDto<ProductRestockRequestDto>> createRestockRequest(
            @RequestBody CreateRestockRequestDto dto) {
        ApiResponseDto<ProductRestockRequestDto> response = productRestockRequestService.createRestockRequest(dto);
        return ResponseEntity.ok(response);
    }

//    @PutMapping("/handle/request/{requestId}")
//    @PreAuthorize("hasAnyAuthority('PLANT_HEAD','CHIEF_SUPERVISOR')")
//    public ResponseEntity<ApiResponseDto<String>> handleToolRequest(
//            @PathVariable Long requestId,
//            @RequestParam boolean approve,
//            @RequestParam(required = false) String reason) {
//
//        ApiResponseDto<String> response =
//                toolRequestService.handleToolRequest(requestId, approve, reason);
//
//        return ResponseEntity.ok(response);
//    }



}
