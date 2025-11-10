package com.project.supply.chain.management.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AddMerchandiseDto {
        private String name;

        private Long requiredPoints;
        private Long availableQuantity;


}
