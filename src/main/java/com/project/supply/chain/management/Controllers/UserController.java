package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.*;
import com.project.supply.chain.management.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    PlantHeadService plantHeadService;
    @Autowired
    ToolService toolService;
    @Autowired
    FactoryService factoryService;
    @Autowired
    CentralOfficeService centralOfficeService;
    @Autowired
    UserService userService;

    @Autowired
    MerchandiseService merchandiseService;
    @Autowired
    ToolCategoryService toolCategoryService;



    //PROFILE OF USERS

    @GetMapping("/get/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName(); // email extracted from token

        ApiResponse<ProfileResponseDto> response = userService.getProfile(currentUserEmail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/upload-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ImageResponseDto> uploadProfileImage(
            @RequestParam("image") MultipartFile image
    ) throws Exception {
        ImageResponseDto response = userService.uploadProfileImage(image);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create/factory")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<Void>> createFactory(@RequestBody FactoryDto factoryDto) {

        //void means that no data payload is expected
        ApiResponse<Void> response = factoryService.createFactory(factoryDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/central-office")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<Void>> createCentralOffice(@RequestBody CentralOfficeDto centralOfficeDto)
    {
        ApiResponse response=centralOfficeService.createCentralOffice(centralOfficeDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/add/central-officer")
    @PreAuthorize("hasAuthority('OWNER')")
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

//get api for owner
    @GetMapping("/get/all/employees")
    @PreAuthorize("hasAuthority('OWNER')")
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

    @GetMapping("/get/central-office")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<List<CentralOfficeResponseDto>>> getCentralOffices() {
        ApiResponse<List<CentralOfficeResponseDto>> response = centralOfficeService.getCentralOffice();
        return ResponseEntity.ok(response);
    }
//remove factory id from request path varibale and then add api
    @PutMapping("/update/{factoryId}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<Void>> updateFactory(
            @PathVariable Long factoryId,
            @RequestBody FactoryDto updateFactoryDto) {

        ApiResponse<Void> response = factoryService.updateFactory(factoryId, updateFactoryDto);
        return ResponseEntity.ok(response);
    }
  //remove path variable in the controller
    @DeleteMapping("/delete/{factoryId}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteFactory(@PathVariable Long factoryId) {
        ApiResponse<Void> response = factoryService.deleteFactory(factoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/factory/production/summary")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<List<FactoryProductionSummaryDto>>> getProductionSummary() {
        ApiResponse<List<FactoryProductionSummaryDto>> response = factoryService.getFactoryProductionSummary();
        return ResponseEntity.ok(response);
    }

    //Merchandise CRUD

    @PostMapping(value = "/add/merchandise", consumes = {"multipart/form-data"})
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
    // Plant head Operations
    //remove the plantheadId
    @PostMapping("/create/bay/{plantHeadId}")
    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    public ResponseEntity<ApiResponse<String>> createBay(
            @PathVariable Long plantHeadId,
            @RequestBody BayRequestDto request
    ) {
        ApiResponse<String> response = plantHeadService.createBay(plantHeadId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/bays")
    public ApiResponse<List<BayListdto>> getAllBays() {
        return plantHeadService.getBaysInFactory();
    }

    @PostMapping("/add/factory/employees")
    public ResponseEntity<ApiResponse<UserResponseDto>> createEmployee(@RequestBody EmployeeRequestDto request) {
        ApiResponse<UserResponseDto> response = plantHeadService.createEmployeeForCurrentPlantHead(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get/factory/employees")
    public ApiResponse<Page<UserResponseDto>> getAllEmployeesInFactory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleStr
    ) {
        return plantHeadService.getEmployeesInFactory(keyword, roleStr, page, size);
    }

    @PostMapping("/factory/update/inventory")
    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    public ResponseEntity<ApiResponse<Void>> updateFactoryStock(@RequestBody UpdateStockRequestDto request) {
        ApiResponse<Void> response = plantHeadService.updateFactoryProductStock(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/factory/products")
    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    public ResponseEntity<ApiResponse<List<FactoryProductStockResponseDto>>> getAllProductsWithStock() {
        ApiResponse<List<FactoryProductStockResponseDto>> response = plantHeadService.getAllProductsWithStock();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/factory/low-stock")
    @PreAuthorize("hasAuthority('PLANT_HEAD')")
    public ResponseEntity<ApiResponse<List<FactoryProductStockResponseDto>>> getLowStockProducts() {
        ApiResponse<List<FactoryProductStockResponseDto>> response = plantHeadService.getLowStockProducts();
        return ResponseEntity.ok(response);
    }
    ///Create Storage Areas























    //Tools Category


    @PostMapping("/tools/add/category")
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

    // CReate ToolS CRUD















    //CHIEF-SUPERVISOR
    @Autowired
    private CheifSupervisorService chiefSupervisorService;

    @PostMapping("/add/worker")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponse<WorkerResponseDto>> addWorker(@RequestBody AddEmployeeDto dto) {
        return ResponseEntity.ok(chiefSupervisorService.addWorker(dto));
    }


    @PutMapping("/update/worker/{workerId}")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponse<WorkerResponseDto>> updateWorker(@PathVariable Long workerId, @RequestBody UpdateEmployeeDto dto) {
        return ResponseEntity.ok(chiefSupervisorService.updateWorker(workerId, dto));
    }

    @DeleteMapping("/delete/worker/{workerId}")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponse<Void>> deleteWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(chiefSupervisorService.softDeleteWorker(workerId));
    }
    @GetMapping("/get/workers")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponse<Page<WorkerResponseDto>>> getAllWorkers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String factoryName,
            @RequestParam(required = false) String bayName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ApiResponse<Page<WorkerResponseDto>> response =
                chiefSupervisorService.searchWorkers(name, factoryName, bayName, page, size);
        return ResponseEntity.ok(response);
    }


}
