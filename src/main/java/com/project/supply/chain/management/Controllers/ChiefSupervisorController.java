package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.CheifSupervisorService;
import com.project.supply.chain.management.dto.AddEmployeeDto;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.UpdateEmployeeDto;
import com.project.supply.chain.management.dto.WorkerResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chief-supervisor")
public class ChiefSupervisorController {

    @Autowired
    private CheifSupervisorService chiefSupervisorService;

    @PostMapping("/add/worker")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<WorkerResponseDto>> addWorker(@RequestBody AddEmployeeDto dto) {
        return ResponseEntity.ok(chiefSupervisorService.addWorker(dto));
    }


    @PutMapping("/update/worker/{workerId}")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<WorkerResponseDto>> updateWorker(@PathVariable Long workerId, @RequestBody UpdateEmployeeDto dto) {
        return ResponseEntity.ok(chiefSupervisorService.updateWorker(workerId, dto));
    }

    @DeleteMapping("/delete/worker/{workerId}")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<Void>> deleteWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(chiefSupervisorService.softDeleteWorker(workerId));
    }
    @GetMapping("/workers")
    @PreAuthorize("hasAuthority('CHIEF_SUPERVISOR')")
    public ResponseEntity<ApiResponseDto<Page<WorkerResponseDto>>> getAllWorkers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String factoryName,
            @RequestParam(required = false) String bayName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ApiResponseDto<Page<WorkerResponseDto>> response =
                chiefSupervisorService.searchWorkers(name, factoryName, bayName, page, size);
        return ResponseEntity.ok(response);
    }


}
