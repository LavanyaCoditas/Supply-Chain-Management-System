package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.ProductRestockRequestService;
import com.project.supply.chain.management.ServiceInterfaces.ProductService;
import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.dto.*;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        ApiResponseDto<ProductResponseDto> response = productService.uploadProduct(productDto, imageFile);
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
    @GetMapping("/central-office/get-inventory")
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE')")
    public ApiResponseDto<Page<CentralOfficeInventoryDto>> getCentralOfficeInventory(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long minQuantity,
            @RequestParam(required = false) Long maxQuantity,
            BaseRequestDto requestDto) {

        return productRestockRequestService.getCentralOfficeInventory(
                productId, productName, minQuantity, maxQuantity, requestDto);
    }

    @PreAuthorize("hasAuthority('CENTRAL_OFFICE')")
    @PostMapping("/central-office/create/restock-request")
    public ApiResponseDto<ProductRestockRequestDto> createRestockRequest(
            @Valid @RequestBody CreateRestockRequestDto requestDto) {
        return productRestockRequestService.createRestockRequest(requestDto);
    }

    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    @PutMapping("/factory/restock-requests/{requestId}/complete")
    public ApiResponseDto<ProductRestockRequestDto> completeRestockRequest(
            @PathVariable Long requestId) {
        return productRestockRequestService.completeRestockRequest(requestId);
    }

    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    @PostMapping("/factories/stock/production")
    public ApiResponseDto<String> updateStockDirectly(@Valid @RequestBody UpdateProductStockDto stockDto) {
        return productRestockRequestService.updateStockDirectly(stockDto);
    }

    @PreAuthorize("hasAuthority('CENTRAL_OFFICE')")
    @GetMapping("/central-office/get/restock-requests")
    public ApiResponseDto<Page<ProductRestockRequestDto>> getMyRestockRequests(
            @RequestParam(required = false) ToolOrProductRequestStatus status,
            BaseRequestDto requestDto) {
        return productRestockRequestService.getMyRestockRequests(status, requestDto);
    }

    @PreAuthorize("hasAnyAuthority('PLANT_HEAD', 'OWNER')")
    @GetMapping("/factories/get/restock-requests")
    public ApiResponseDto<Page<ProductRestockRequestDto>> getMyFactoryRestockRequests(
            @RequestParam(required = false) ToolOrProductRequestStatus status,
            BaseRequestDto requestDto) {
        return productRestockRequestService.getMyFactoryRestockRequests(status, requestDto);
    }




}
