package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.ProductCategoryService;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ProductCategoryDto;
import com.project.supply.chain.management.entity.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-category")
public class ProductCategoryController {

    @Autowired
    ProductCategoryService categoryService;


    @PostMapping("/create")
    public ApiResponse<Void> createProductCategory(@RequestBody ProductCategoryDto productCategoryDto) {
        return categoryService.createProductCategory(productCategoryDto);
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> updateProductCategory(
            @PathVariable Long categoryId,
            @RequestBody ProductCategoryDto dto) {
        ApiResponse<Void> response = categoryService.updateProductCategory(categoryId, dto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get/categories")

    public ResponseEntity<ApiResponse<List<ProductCategory>>> getAllCategories(
            @RequestParam(defaultValue = "categoryName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        ApiResponse<List<ProductCategory>> response = categoryService.getAllCategories(sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductCategory(@PathVariable Long categoryId) {
        ApiResponse<Void> response = categoryService.deleteProductCategory(categoryId);
        return ResponseEntity.ok(response);
    }




}
