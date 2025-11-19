
package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerResponseDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String factoryName;
    private String bayName;
}
