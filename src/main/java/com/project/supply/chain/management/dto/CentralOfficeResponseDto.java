package com.project.supply.chain.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CentralOfficeResponseDto {
    private Long id;
    private String location;
    private List<UserListDto> officers;
}
