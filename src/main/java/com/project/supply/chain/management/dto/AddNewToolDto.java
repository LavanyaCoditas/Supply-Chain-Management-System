// 📁 com.project.supply.chain.management.dto.AddNewToolDto.java
package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.constants.ToolType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddNewToolDto {
    //tool dto
    private String name;
    private Long categoryId;
    private ToolType type;
    private Expensive isExpensive;
    private Integer threshold;
    private Integer quantity; // Only for PLANT_HEAD
    private Long storageAreaId;//Only for PLANT_HEAD
    private MultipartFile image;
}
