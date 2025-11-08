package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.PlantHeadService;
import com.project.supply.chain.management.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plant-head")
public class PlantHeadController {

    @Autowired
    PlantHeadService plantHeadService;
    //Create a Bay for factory
    @PostMapping("/bays")
    public ApiResponse<String> createBay(
            @RequestParam("plantHeadId") Long plantHeadId,
            @RequestBody BayRequestDto request) {

      return plantHeadService.createBay(plantHeadId, request);

    }

    @GetMapping("/get/bays")
    public ApiResponse<List<BayListdto>> getAllBays() {
        return plantHeadService.getBaysInFactory();
    }

    @PostMapping("/add/employees")
    public ResponseEntity<ApiResponse<UserResponseDto>> createEmployee(@RequestBody EmployeeRequestDto request) {
        ApiResponse<UserResponseDto> response = plantHeadService.createEmployeeForCurrentPlantHead(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/employees")
    public ApiResponse<Page<UserResponseDto>> getAllEmployeesInFactory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleStr
    ) {
        return plantHeadService.getEmployeesInFactory(keyword, roleStr, page, size);
    }
}
