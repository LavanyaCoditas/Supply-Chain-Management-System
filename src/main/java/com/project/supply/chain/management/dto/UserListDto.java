package com.project.supply.chain.management.dto;


import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.constants.Account_Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserListDto {
        private Long id;
        private String username;
        private String email;
        private Role role;
        private Account_Status isActive;
        private String img;
        private Long phone;
    }

