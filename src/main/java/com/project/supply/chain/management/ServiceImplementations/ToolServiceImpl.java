package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.ToolService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.*;
import com.project.supply.chain.management.exceptions.ResourceAlreadyExistsException;
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import com.project.supply.chain.management.exceptions.UnauthorizedAccessException;
import com.project.supply.chain.management.exceptions.UserNotFoundException;
import com.project.supply.chain.management.specifications.ToolSpecifications;
import com.project.supply.chain.management.util.ApplicationUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ToolServiceImpl implements ToolService {

   private final CloudinaryService cloudinaryService;

    private final  ToolCategoryRepository toolCategoryRepository;

    private final  ToolsRepository toolRepository;

    private final ToolStockRepository toolStockRepository;

    private final UserFactoryMappingRepository userFactoryMappingRepository;

    private final ApplicationUtils appUtils;

    @Override
    @Transactional
    public ApiResponseDto<ToolResponseDto> createTool(ToolDto dto) {
        String email = appUtils.getLoggedInUserEmail();
        User user = appUtils.getUser(email);

        if (user == null || user.getRole() != Role.OWNER)
            throw new UnauthorizedAccessException("Only OWNER can create tools");

        ToolCategory category = toolCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Tool category not found"));

        if (toolRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Tool with name '" + dto.getName() + "already exists");
        }
        Tool tool = new Tool();
        tool.setName(dto.getName());
        tool.setCategory(category);
        tool.setType(dto.getType());
        tool.setIsExpensive(dto.getIsExpensive());
        tool.setThreshold(dto.getThreshold());
        tool.setIsActive(Account_Status.ACTIVE);
        tool.setCreatedAt(LocalDateTime.now());
        tool.setUpdatedAt(LocalDateTime.now());

        toolRepository.save(tool);

        ToolResponseDto response = new ToolResponseDto(
                tool.getId(),
                tool.getName(),
                category.getName(),
                tool.getType(),
                tool.getIsExpensive(),
                tool.getThreshold(),
                tool.getImageUrl()

        );

        return new ApiResponseDto<>(true, "Tool created successfully", response);
    }

    @Override
    @Transactional
    public ApiResponseDto<ToolResponseDto> updateToolImage(Long toolId, MultipartFile image) throws IOException {
        String email = appUtils.getLoggedInUserEmail();
        User user = appUtils.getUser(email);
        if (user == null || user.getRole() != Role.OWNER)
            throw new UnauthorizedAccessException("Only OWNER can update tool images");

        Tool tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("Tool not found"));

        String imageUrl = cloudinaryService.uploadImage(image);
        tool.setImageUrl(imageUrl);
        toolRepository.save(tool);

        ToolResponseDto response = new ToolResponseDto(
                tool.getId(),
                tool.getName(),
                tool.getCategory().getName(),
                tool.getType(),
                tool.getIsExpensive(),
                tool.getThreshold(),
                tool.getImageUrl()
        );
        return new ApiResponseDto<>(true, "Tool image updated successfully", response);
    }

    @Override
    @Transactional
    public ApiResponseDto<ToolResponseDto> updateTool(Long toolId, ToolDto dto) {

        String email = appUtils.getLoggedInUserEmail();
        User user =appUtils.getUser(email);

        if (user == null) throw new UserNotFoundException("User not found");
        //  Only OWNER can update tool
        if (user.getRole() != Role.OWNER) {
            throw new UnauthorizedAccessException("Only Owner can update tools");
        }
        // Find tool
        Tool tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("Tool not found"));
        //  Validate category
        ToolCategory category = toolCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Tool category not found"));
        //  Update fields
        tool.setName(dto.getName());
        tool.setCategory(category);
        tool.setType(dto.getType());
        tool.setIsExpensive(dto.getIsExpensive());
        tool.setThreshold(dto.getThreshold());

        toolRepository.save(tool);
        ToolResponseDto response = new ToolResponseDto(
                tool.getId(),
                tool.getName(),
                category.getName(),
                tool.getType(),
                tool.getIsExpensive(),
                tool.getThreshold(),
                tool.getImageUrl()
        );

        return new ApiResponseDto<>(true, "Tool updated successfully by Owner", response);
    }

    @Override
    @Transactional
    public ApiResponseDto<String> addToolToFactoryStock(ToolInventoryStockDto dto) {
        //  Logged-in user
        String email = appUtils.getLoggedInUserEmail();
        User user=appUtils.getUser(email);

        if (user == null) throw new UserNotFoundException("User not found");
        if (user.getRole() != Role.PLANT_HEAD) {
            throw new UnauthorizedAccessException("Only Plant Head can add tools to factory stock");
        }

        // Find factory of this Plant Head
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Factory mapping not found for this user"));
        Factory factory = mapping.getFactory();

        //  Find tool
        Tool tool = toolRepository.findById(dto.getToolId())
                .orElseThrow(() -> new ResourceNotFoundException("Tool not found"));

        // Check if tool already exists in factory stock
        ToolStock existingStock = toolStockRepository.findByToolAndFactory(tool, factory).orElse(null);

        if (existingStock != null) {
            // Update existing stock quantity
            existingStock.setTotalQuantity(existingStock.getTotalQuantity() + dto.getQuantity());
            existingStock.setAvailableQuantity(existingStock.getAvailableQuantity()+dto.getQuantity());
            existingStock.setLastUpdatedAt(LocalDateTime.now());
            toolStockRepository.save(existingStock);
        } else {
            //  Create new stock entry
            ToolStock newStock = new ToolStock();
            newStock.setTool(tool);
            newStock.setFactory(factory);
            newStock.setTotalQuantity(dto.getQuantity());
            newStock.setLastUpdatedAt(LocalDateTime.now());
            newStock.setAvailableQuantity(dto.getQuantity());
            toolStockRepository.save(newStock);
        }

        return new ApiResponseDto<>(true, "Tool added to factory stock successfully", null);
    }

    @Override
    public ApiResponseDto<List<GetToolDto>> getAllToolsForOwner(
            String searchName,
            String categoryName,
            String type,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        String email = appUtils.getLoggedInUserEmail();
        User user =appUtils.getUser(email);
        if (user == null || user.getRole() != Role.OWNER) {
            throw new UnauthorizedAccessException("Only Owner can view all tools");
        }

        Specification<Tool> spec = Specification.allOf(
                ToolSpecifications.searchByName(searchName),
                ToolSpecifications.searchByCategory(categoryName),
                ToolSpecifications.searchByType(type)
        );

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Tool> pageResult = toolRepository.findAll(spec, pageable);

        List<GetToolDto> dtos = pageResult.getContent().stream()
                .map(t -> GetToolDto.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .categoryName(t.getCategory().getName())
                        .type(t.getType())
                        .isExpensive(t.getIsExpensive())
                        .threshold(t.getThreshold())
                        .imageUrl(t.getImageUrl())
                        .build())
                .toList();

        return new ApiResponseDto<>(true, "Tools fetched successfully", dtos);
    }

    @Override
    public ApiResponseDto<String> softDeleteTool(Long id) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool not found"));

        if (tool.getIsActive() == Account_Status.IN_ACTIVE) {
            return new ApiResponseDto<>(false, "Already inactive", null);
        }
        tool.setIsActive(Account_Status.IN_ACTIVE);
        tool.setUpdatedAt(LocalDateTime.now());
        toolRepository.save(tool);
        return new ApiResponseDto<>(true, "Tool deleted", "INACTIVE");
    }



}
