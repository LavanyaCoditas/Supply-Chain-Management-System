// 📁 com.project.supply.chain.management.ServiceImplementations.ToolServiceImpl.java
package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.ToolService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
//all args
public class ToolServiceImpl implements ToolService {
    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
     StorageAreaRepository storageAreaRepository;
      @Autowired
     UserRepository userRepository;
      @Autowired
     UserFactoryMappingRepository userFactoryMappingRepository;
      @Autowired
      ToolsRepository toolRepository;
      @Autowired
      ToolCategoryRepository toolCategoryRepository;

      @Autowired
      ToolStorageMappingRepository toolStorageMappingRepository;


    @Override
    public ApiResponse<String> createStorageArea(CreateStorageAreaDto dto) {
        //  Get logged-in Plant Head
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        if (user == null || user.getRole() != Role.PLANT_HEAD) {
            throw new RuntimeException("Only Plant Head can create storage areas");
        }

        //  Get factory mapped to this Plant Head
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));

        Factory factory = mapping.getFactory();


        boolean exists = storageAreaRepository.existsByFactoryAndRowNumAndColNumAndStack(
                factory,
                dto.getRowNum(),
                dto.getColNum(),
                dto.getStack()
        );

        if (exists) {
            return new ApiResponse<>(false,
                    "Storage area already exists for this factory with the same row, column, and stack",
                    null);
        }

        //  Validate sequence continuity
        int row = dto.getRowNum();
        int col = dto.getColNum();
        int stack = dto.getStack();

        // 1️⃣ Row continuity
        if (row > 1 && !storageAreaRepository.existsByFactoryAndRowNum(factory, row - 1)) {
            return new ApiResponse<>(false,
                    String.format("Cannot create Row %d without Row %d existing", row, row - 1),
                    null);
        }

        // 2️⃣ Column continuity (within same row)
        if (col > 1 && !storageAreaRepository.existsByFactoryAndRowNumAndColNum(factory, row, col - 1)) {
            return new ApiResponse<>(false,
                    String.format("Cannot create Column %d in Row %d without Column %d existing", col, row, col - 1),
                    null);
        }

        // 3️⃣ Stack continuity (within same row & column)
        if (stack > 1 && !storageAreaRepository.existsByFactoryAndRowNumAndColNumAndStack(factory, row, col, stack - 1)) {
            return new ApiResponse<>(false,
                    String.format("Cannot create Stack %d in Row %d Column %d without Stack %d existing",
                            stack, row, col, stack - 1),
                    null);
        }

        // ✅ Generate Bucket Name
        String bucketName = String.format("BUCKET-R%dC%dS%d", row, col, stack);

        // ✅ Create and save storage area
        StorageArea storageArea = new StorageArea();
        storageArea.setFactory(factory);
        storageArea.setRowNum(row);
        storageArea.setColNum(col);
        storageArea.setStack(stack);
        storageArea.setBucket(bucketName);

        storageAreaRepository.save(storageArea);

        return new ApiResponse<>(true,
                "Storage area created successfully with bucket name: " + bucketName,
                bucketName);
    }

    @Override
    public ApiResponse<List<StorageAreaResponseDto>> getAllStorageAreasForPlantHead() {
        //  Get logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        //  Allow only Plant Head or Chief Supervisor
        if (user.getRole() != Role.PLANT_HEAD && user.getRole() != Role.CHIEF_SUPERVISOR) {
            throw new RuntimeException("Only Plant Head or Chief Supervisor can view storage areas");
        }

        //  Get factory for this user (both Plant Head and Chief Supervisor are mapped to a factory)
        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User is not mapped to any factory"));

        Factory factory = mapping.getFactory();

        //  Fetch all storage areas for this factory
        List<StorageArea> storageAreas = storageAreaRepository.findByFactory(factory);

        //  Map to DTOs
        List<StorageAreaResponseDto> response = storageAreas.stream()
                .map(area -> new StorageAreaResponseDto(
                        area.getId(),
                        area.getBucket(),
                        area.getRowNum(),
                        area.getColNum(),
                        area.getStack(),
                        factory.getName()
                ))
                .toList();

        return new ApiResponse<>(true, "Storage areas fetched successfully", response);
    }


    @Override
    @Transactional
    public ApiResponse<ToolResponseDto> createTool(AddNewToolDto dto) throws IOException {



            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email);

            if (user == null) throw new RuntimeException("User not found");

            ToolCategory category = toolCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Tool category not found"));

            Tool existingTool = toolRepository.findByNameIgnoreCase(dto.getName()).orElse(null);

            // 1. Upload image to Cloudinary (if provided)
            String imageUrl = cloudinaryService.uploadImage(dto.getImage());

            //  2. Owner creating tool
            if (user.getRole() == Role.OWNER) {
                if (existingTool != null) {
                    return new ApiResponse<>(false, "Tool with this name already exists", null);
                }

                Tool tool = new Tool();
                tool.setName(dto.getName());
                tool.setCategory(category);
                tool.setType(dto.getType());
                tool.setIsExpensive(dto.getIsExpensive());
                tool.setThreshold(dto.getThreshold());
                tool.setQty(0);
                tool.setIsActive(Account_Status.ACTIVE);
                tool.setImageUrl(imageUrl); //  store uploaded URL

                toolRepository.save(tool);

                ToolResponseDto response = new ToolResponseDto(
                        tool.getId(),
                        tool.getName(),
                        category.getName(),
                        tool.getType().name(),
                        tool.getIsExpensive(),
                        tool.getThreshold(),
                        0,
                        null,
                        null,
                        imageUrl
                );

                return new ApiResponse<>(true, "Tool created successfully by Owner", response);
            }

            //  3. Plant Head creating tool
            else if (user.getRole() == Role.PLANT_HEAD) {
                UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(user)
                        .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));
                Factory factory = mapping.getFactory();

                StorageArea storageArea = storageAreaRepository.findById(dto.getStorageAreaId())
                        .orElseThrow(() -> new RuntimeException("Storage area not found"));

                Tool tool = existingTool;
                if (tool == null) {
                    tool = new Tool();
                    tool.setName(dto.getName());
                    tool.setCategory(category);
                    tool.setType(dto.getType());
                    tool.setIsExpensive(dto.getIsExpensive());
                    tool.setThreshold(dto.getThreshold());
                    tool.setQty(dto.getQuantity());
                    tool.setIsActive(Account_Status.ACTIVE);
                    tool.setImageUrl(imageUrl);
                    toolRepository.save(tool);
                }

                ToolStorageMapping mappingEntry = new ToolStorageMapping();
                mappingEntry.setTool(tool);
                mappingEntry.setFactory(factory);
                mappingEntry.setStorageArea(storageArea);
                toolStorageMappingRepository.save(mappingEntry);

                ToolResponseDto response = new ToolResponseDto(
                        tool.getId(),
                        tool.getName(),
                        category.getName(),
                        tool.getType().name(),
                        tool.getIsExpensive(),
                        tool.getThreshold(),
                        tool.getQty(),
                        storageArea.getBucket(),
                        factory.getName(),
                        tool.getImageUrl()
                );

                return new ApiResponse<>(true, "Tool created successfully by Plant Head", response);
            }

            return new ApiResponse<>(false, "Only Owner or Plant Head can create tools", null);
    }
    @Override
    @Transactional
    public ApiResponse<ToolResponseDto> assignToolToFactory(AssignToolToFactoryDto dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        if (user == null || user.getRole() != Role.PLANT_HEAD) {
            throw new RuntimeException("Only Plant Heads can assign tools to factories");
        }

        UserFactoryMapping mapping = userFactoryMappingRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Plant Head is not mapped to any factory"));
        Factory factory = mapping.getFactory();

        Tool tool = toolRepository.findById(dto.getToolId())
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        StorageArea storageArea = storageAreaRepository.findById(dto.getStorageAreaId())
                .orElseThrow(() -> new RuntimeException("Storage Area not found"));

        if (!storageArea.getFactory().getId().equals(factory.getId())) {
            throw new RuntimeException("Storage Area does not belong to this factory");
        }

        Optional<ToolStorageMapping> existingMapping =
                toolStorageMappingRepository.findByFactoryAndTool(factory, tool);

        ToolStorageMapping mappingEntity;
        int finalQuantity;

        if (existingMapping.isPresent()) {
            // Update quantity if already assigned
            mappingEntity = existingMapping.get();
           tool.setQty(mappingEntity.getTool().getQty() + dto.getQuantity());
            finalQuantity = tool.getQty();
        } else {
            // Create a new mapping for this factory
            mappingEntity = new ToolStorageMapping();
            mappingEntity.setFactory(factory);
            mappingEntity.setTool(tool);
            mappingEntity.setStorageArea(storageArea);

            mappingEntity.setCreatedAt(LocalDateTime.now());

            finalQuantity = dto.getQuantity();
        }

        toolStorageMappingRepository.save(mappingEntity);

        ToolResponseDto response = new ToolResponseDto(
                tool.getId(),
                tool.getName(),
                tool.getCategory().getName(),
                tool.getType().name(),
                tool.getIsExpensive(),
                tool.getThreshold(),
                finalQuantity,
                storageArea.getBucket(),
                factory.getName(),
                tool.getImageUrl()
        );
        return new ApiResponse<>(true, "Tool successfully assigned to factory", response);
    }

}
