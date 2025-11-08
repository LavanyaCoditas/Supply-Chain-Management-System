package com.project.supply.chain.management.dto;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CentralOfficeDto {
    private String location;
    private String centralOfficeHeadEmail;
    private String centralOfficeHeadName;
    private String password;
}

