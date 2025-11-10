package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.CentralOfficeService;
import com.project.supply.chain.management.ServiceInterfaces.FactoryService;
import com.project.supply.chain.management.ServiceInterfaces.MerchandiseService;
import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.dto.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasAuthority('OWNER')")
public class OwnerController

{
    @Autowired
    FactoryService factoryService;
    @Autowired
    CentralOfficeService centralOfficeService;
    @Autowired
    UserService userService;

    @Autowired
    MerchandiseService merchandiseService;


    @PostMapping("/create/factory")
public ResponseEntity<ApiResponse<Void>> createFactory(@RequestBody FactoryDto factoryDto) {

    //void means that no data payload is expected
    ApiResponse<Void> response = factoryService.createFactory(factoryDto);
    return ResponseEntity.ok(response);
}

@PostMapping("/create/central-office")
    public ResponseEntity<ApiResponse<Void>> createCentralOffice(@RequestBody CentralOfficeDto centralOfficeDto)
{
   ApiResponse response=centralOfficeService.createCentralOffice(centralOfficeDto);

   return ResponseEntity.ok(response);
}
    @PostMapping("/add-officer")
    public ResponseEntity<ApiResponse<Void>> addCentralOfficer(@RequestBody AddCentralOfficerDto addCentralOfficerDto) {
        ApiResponse<Void> response = centralOfficeService.addCentralOfficerToOffice(addCentralOfficerDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create/plant-head")
    public ResponseEntity<ApiResponse<Void>> createPlanthead(@RequestBody AddEmployeeDto dto) {
        ApiResponse<Void> response = factoryService.createEmployeeAsPlantHead(dto);
        return ResponseEntity.ok(response);
    }
  @GetMapping("/get/factories")
    public ResponseEntity<ApiResponse<Page<FactoryDto>>> getAllFactories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort
    ) {
        // handle sort (e.g., sort=name,asc)
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        ApiResponse<Page<FactoryDto>> response = factoryService.getAllFactories(search, pageable);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get/employees")
    public ResponseEntity<ApiResponse<Page<UserListDto>>> getAllEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long factoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ApiResponse<Page<UserListDto>> response = userService.getAllEmployees(search, role, factoryId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/central-offices")
    public ResponseEntity<ApiResponse<List<CentralOfficeResponseDto>>> getCentralOffices() {
        ApiResponse<List<CentralOfficeResponseDto>> response = centralOfficeService.getCentralOffice();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{factoryId}")
    public ResponseEntity<ApiResponse<Void>> updateFactory(
            @PathVariable Long factoryId,
            @RequestBody FactoryDto updateFactoryDto) {

        ApiResponse<Void> response = factoryService.updateFactory(factoryId, updateFactoryDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{factoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteFactory(@PathVariable Long factoryId) {
        ApiResponse<Void> response = factoryService.deleteFactory(factoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/production/summary")

    public ResponseEntity<ApiResponse<List<FactoryProductionSummaryDto>>> getProductionSummary() {
        ApiResponse<List<FactoryProductionSummaryDto>> response = factoryService.getFactoryProductionSummary();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE')")
    public ResponseEntity<ApiResponse<MerchandiseResponseDto>> addMerchandise(
            @ModelAttribute AddMerchandiseDto dto,
            @RequestPart("image") MultipartFile imageFile
    ) throws Exception {
        ApiResponse<MerchandiseResponseDto> response = merchandiseService.addMerchandise(dto, imageFile);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/merchandise")
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE','DISTRIBUTOR')")
    public ResponseEntity<ApiResponse<Page<MerchandiseResponseDto>>> getAllMerchandise(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort // rewardPointsAsc / rewardPointsDesc
    ) {
        ApiResponse<Page<MerchandiseResponseDto>> response =
                merchandiseService.getAllMerchandise(page, size, search, sort);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/update/merchandise/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE')")
    public ResponseEntity<ApiResponse<MerchandiseResponseDto>> updateMerchandise(
            @PathVariable Long id,
            @ModelAttribute AddMerchandiseDto dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws Exception {
        ApiResponse<MerchandiseResponseDto> response = merchandiseService.updateMerchandise(id, dto, imageFile);
        return ResponseEntity.ok(response);
    }

    // ✅ Soft Delete Merchandise
    @DeleteMapping("/delete/merchandise/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE')")
    public ResponseEntity<ApiResponse<Void>> deleteMerchandise(@PathVariable Long id) {
        ApiResponse<Void> response = merchandiseService.softDeleteMerchandise(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/restock/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'CENTRAL_OFFICE')")
    public ResponseEntity<ApiResponse<MerchandiseResponseDto>> restockMerchandise(
            @PathVariable Long id,
            @RequestParam Long additionalQuantity
    ) {
        ApiResponse<MerchandiseResponseDto> response = merchandiseService.restockMerchandise(id, additionalQuantity);
        return ResponseEntity.ok(response);
    }


    //Tools


}
