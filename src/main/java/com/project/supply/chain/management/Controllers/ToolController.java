package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.ToolCategoryService;
import com.project.supply.chain.management.ServiceInterfaces.ToolRequestService;
import com.project.supply.chain.management.ServiceInterfaces.ToolService;
import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tool")
@AllArgsConstructor
public class ToolController
{

    private final ToolService toolService;

    private final ToolCategoryService toolCategoryService;

   private final ToolRequestService toolRequestService;


    @PostMapping("/create/tool-category")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD')")
    public ResponseEntity<ApiResponseDto<ToolCategoryDto>> createToolCategory(@RequestBody AddToolCategoryDto dto)
    {
        ApiResponseDto<ToolCategoryDto> response = toolCategoryService.addToolCategory(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/tool-categories")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD', 'CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<List<ToolCategoryDto>>> getAllToolCategories() {
        ApiResponseDto<List<ToolCategoryDto>> response = toolCategoryService.getAllToolCategories();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/tool-category/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD')")
    public ResponseEntity<ApiResponseDto<ToolCategoryDto>> updateToolCategory(@PathVariable Long id, @RequestBody AddToolCategoryDto dto) {
        ApiResponseDto<ToolCategoryDto> response = toolCategoryService.updateToolCategory(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/tool-category/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD')")
    public ResponseEntity<ApiResponseDto<Void>> deleteToolCategory(@PathVariable Long id) {
        ApiResponseDto<Void> response = toolCategoryService.deleteToolCategory(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponseDto<ToolResponseDto>> createTool(@RequestBody ToolDto dto) throws IOException {
        ApiResponseDto<ToolResponseDto> response = toolService.createTool(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{toolId}/update-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponseDto<ToolResponseDto>> updateToolImage(
            @PathVariable Long toolId,
            @RequestParam("image") MultipartFile image) throws IOException {

        ApiResponseDto<ToolResponseDto> response = toolService.updateToolImage(toolId, image);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{toolId}")
    @PreAuthorize("hasAnyAuthority('OWNER')")
    public ResponseEntity<ApiResponseDto<ToolResponseDto>> updateTool(
            @PathVariable Long toolId,
            @RequestBody ToolDto dto) {
        ApiResponseDto<ToolResponseDto> response = toolService.updateTool(toolId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/all/tools")
    @PreAuthorize("hasAnyAuthority('OWNER','PLANT_HEAD')")
    public ResponseEntity<ApiResponseDto<List<GetToolDto>>> getAllToolsForOwner(
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        ApiResponseDto<List<GetToolDto>> response = toolService.getAllToolsForOwner(
                searchName, categoryName, type, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(response);
    }


    @PostMapping("/factory/stock/add")
    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    public ResponseEntity<ApiResponseDto<String>> addToolToFactoryStock(@RequestBody ToolInventoryStockDto dto) {
        ApiResponseDto<String> response = toolService.addToolToFactoryStock(dto);
        return ResponseEntity.ok(response);
    }


    //Tool REQUEST BY WORKER
    @PostMapping("/create/tool/request")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<ApiResponseDto<String>> requestTool(@RequestBody ToolRequestDto dto) {
        return ResponseEntity.ok(toolRequestService.requestTool(dto));
    }

    @PutMapping("/handle/{requestId}")
    @PreAuthorize("hasAnyAuthority('PLANT_HEAD','CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<String>> handleToolRequest(
            @PathVariable Long requestId,
            @RequestParam boolean approve,
            @RequestParam(required = false) String reason) {

        return ResponseEntity.ok(toolRequestService.handleToolRequest(requestId, approve, reason));
    }
}
