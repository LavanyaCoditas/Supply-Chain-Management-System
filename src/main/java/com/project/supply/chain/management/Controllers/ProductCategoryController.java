package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.ProductCategoryService;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.ProductCategoryDto;
import com.project.supply.chain.management.dto.ProductCategoryResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-category")
public class ProductCategoryController {

    @Autowired
    ProductCategoryService categoryService;

    @PostMapping("/create")
    public ApiResponseDto<Void> createProductCategory(@RequestBody ProductCategoryDto productCategoryDto) {
        return categoryService.createProductCategory(productCategoryDto);
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<ApiResponseDto<Void>> updateProductCategory(
            @PathVariable Long categoryId,
            @RequestBody ProductCategoryDto dto) {
        ApiResponseDto<Void> response = categoryService.updateProductCategory(categoryId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/categories")
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE', 'PLANT_HEAD', 'DISTRIBUTOR')")
    public ResponseEntity<ApiResponseDto<List<ProductCategoryResponseDto>>> getAllCategories(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        ApiResponseDto<List<ProductCategoryResponseDto>> response = categoryService.getAllCategories(sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteProductCategory(@PathVariable Long categoryId) {
        ApiResponseDto<Void> response = categoryService.deleteProductCategory(categoryId);
        return ResponseEntity.ok(response);
    }




}
