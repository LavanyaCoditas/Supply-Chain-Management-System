package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.ToolCategoryService;
import com.project.supply.chain.management.ServiceInterfaces.ToolService;
import com.project.supply.chain.management.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tool")
public class ToolController {
    @Autowired
    ToolService toolService;
    @Autowired
    ToolCategoryService toolCategoryService;



    @PostMapping("/create/tool-category")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD')")
    public ResponseEntity<ApiResponse<ToolCategoryDto>> createToolCategory(
            @RequestBody AddToolCategoryDto dto
    ) {
        ApiResponse<ToolCategoryDto> response = toolCategoryService.addToolCategory(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/tool-categories")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD', 'CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<ToolCategoryDto>>> getAllToolCategories() {
        ApiResponse<List<ToolCategoryDto>> response = toolCategoryService.getAllToolCategories();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/tool-category/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD')")
    public ResponseEntity<ApiResponse<ToolCategoryDto>> updateToolCategory(@PathVariable Long id, @RequestBody AddToolCategoryDto dto) {
        ApiResponse<ToolCategoryDto> response = toolCategoryService.updateToolCategory(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/tool-category/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'PLANT_HEAD')")
    public ResponseEntity<ApiResponse<Void>> deleteToolCategory(@PathVariable Long id) {
        ApiResponse<Void> response = toolCategoryService.deleteToolCategory(id);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/create/tools")
    @PreAuthorize("hasAnyAuthority('OWNER','PLANT_HEAD')")
    public ResponseEntity<ApiResponse<ToolResponseDto>> createTool(@RequestBody AddNewToolDto dto) throws IOException {
        ApiResponse<ToolResponseDto> response = toolService.createTool(dto);
        return ResponseEntity.ok(response);
    }


    //STORAGE CRUD


    @PostMapping("/create/storage-area")
    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    public ResponseEntity<ApiResponse<String>> createStorageArea(@RequestBody CreateStorageAreaDto dto) {
        ApiResponse<String> response = toolService.createStorageArea(dto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get/storage-areas")
    @PreAuthorize("hasAnyAuthority('PLANT_HEAD', 'CHIEF_SUPERVISOR')")
    public ApiResponse<List<StorageAreaResponseDto>> getAllStorageAreas() {
        return toolService.getAllStorageAreasForPlantHead();
    }
    @PostMapping("/factory/get-tool")
    public ResponseEntity<ApiResponse<ToolResponseDto>> assignToolToFactory(
            @RequestBody AssignToolToFactoryDto dto) {
        return ResponseEntity.ok(toolService.assignToolToFactory(dto));
    }
}
