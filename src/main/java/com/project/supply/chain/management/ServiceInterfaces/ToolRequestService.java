package com.project.supply.chain.management.ServiceInterfaces;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.GetToolRequestDto;
import com.project.supply.chain.management.dto.ToolRequestDto;

import java.util.List;

public interface ToolRequestService {
    ApiResponseDto<String> requestTool(ToolRequestDto dto);
   ApiResponseDto<String> handleToolRequest(Long requestId, boolean approve, String reason);
    ApiResponseDto<List<GetToolRequestDto>> getPendingRequestsForApprover(
            String searchWorker,
            String searchTool,
            ToolOrProductRequestStatus status,
            int page,
            int size,
            String sortBy,
            String sortDir
    );


}
