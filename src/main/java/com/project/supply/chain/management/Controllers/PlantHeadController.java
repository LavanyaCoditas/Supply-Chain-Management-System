package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.PlantHeadService;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.BayRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
