package com.project.supply.chain.management.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class AddEmployeeDto {
        private String email;
        private String username;
        private Long phone;
        private Long BayId;

    }


