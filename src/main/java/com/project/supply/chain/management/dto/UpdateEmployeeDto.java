// 📁 com.project.supply.chain.management.dto.UpdateEmployeeDto.java
package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeDto {
    private String username;
    private String email;
    private Long phone;
}
