
    package com.project.supply.chain.management.ServiceImplementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.supply.chain.management.Repositories.MerchandiseRepository;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import com.project.supply.chain.management.exceptions.UnauthorizedAccessException;
import com.project.supply.chain.management.exceptions.UserNotFoundException;
import com.project.supply.chain.management.specifications.MerchandiseSpecifications;
import com.project.supply.chain.management.util.ApplicationUtils;
import com.project.supply.chain.management.util.CloudinaryConfig;
import com.project.supply.chain.management.dto.AddMerchandiseDto;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.dto.MerchandiseResponseDto;
import com.project.supply.chain.management.entity.Merchandise;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.ServiceInterfaces.MerchandiseService;
import com.project.supply.chain.management.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

    @Service
    @AllArgsConstructor
    public class MerchandiseServiceImpl implements MerchandiseService {

        private final MerchandiseRepository merchandiseRepository;
        private final CloudinaryConfig cloudinaryConfig;
        private final UserRepository userRepository;
        private final ApplicationUtils appUtils;


        @Override
        public ApiResponseDto<MerchandiseResponseDto> addMerchandise(AddMerchandiseDto dto, MultipartFile image) throws IOException {

            User user = appUtils.getUser(appUtils.getLoggedInUserEmail());
            if (user == null) {
                throw  new UserNotFoundException("User not found");
            }

            if (merchandiseRepository.existsByNameIgnoreCase(dto.getName())) {
                return new ApiResponseDto<>(false, "Merchandise with this name already exists", null);
            }

            // Upload to Cloudinary
            Cloudinary cloudinary = cloudinaryConfig.cloudinary();
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                    ObjectUtils.asMap("folder", "merchandise"));
            String imageUrl = (String) uploadResult.get("secure_url");

            Merchandise merchandise = new Merchandise();
            merchandise.setName(dto.getName());

            merchandise.setRewardPoints(dto.getRequiredPoints());
            merchandise.setQuantity(dto.getAvailableQuantity());
            merchandise.setImage(imageUrl);
            merchandise.setIsActive(Account_Status.ACTIVE);

            merchandise.setCreatedAt(LocalDateTime.now());

            Merchandise saved = merchandiseRepository.save(merchandise);

            MerchandiseResponseDto response = new MerchandiseResponseDto(
                    saved.getId(),
                    saved.getName(),
                    saved.getRewardPoints(),
                    saved.getQuantity(),
                    saved.getImage()

            );

            return new ApiResponseDto<>(true, "Merchandise added successfully", response);
        }


        @Override
        public ApiResponseDto<Page<MerchandiseResponseDto>> getAllMerchandise(
                int page,
                int size,
                String search,
                Integer minRewardPoints,
                Integer maxRewardPoints,
                String stockStatus,
                String sort) {

            Pageable pageable;

            // Sorting
            if ("rewardPointsAsc".equalsIgnoreCase(sort)) {
                pageable = PageRequest.of(page, size, Sort.by("rewardPoints").ascending());
            } else if ("rewardPointsDesc".equalsIgnoreCase(sort)) {
                pageable = PageRequest.of(page, size, Sort.by("rewardPoints").descending());
            } else {
                pageable = PageRequest.of(page, size, Sort.by("id").descending());
            }

            //  Specification
            Specification<Merchandise> spec = (root, query, cb) -> cb.conjunction();

            // Filter by Active Merchandise
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActive"), Account_Status.ACTIVE)
            );

            // Search by Name
            if (search != null && !search.isBlank()) {
                spec = spec.and(MerchandiseSpecifications.searchByName(search));
            }

            // Filter by Reward Points
            if (minRewardPoints != null) {
                spec = spec.and(MerchandiseSpecifications.hasMinRewardPoints(minRewardPoints));
            }

            if (maxRewardPoints != null) {
                spec = spec.and(MerchandiseSpecifications.hasMaxRewardPoints(maxRewardPoints));
            }

            // Filter by Stock Status
            if (stockStatus != null) {
                if (stockStatus.equalsIgnoreCase("IN_STOCK")) {
                    spec = spec.and((root, query, cb) -> cb.greaterThan(root.get("quantity"), 0));
                } else if (stockStatus.equalsIgnoreCase("OUT_OF_STOCK")) {
                    spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("quantity"), 0));
                }
            }

            //  Fetch from DB
            Page<Merchandise> merchandisePage = merchandiseRepository.findAll(spec, pageable);

            // Map to DTO
            Page<MerchandiseResponseDto> dtoPage = merchandisePage.map(m -> new MerchandiseResponseDto(
                    m.getId(),
                    m.getName(),
                    m.getRewardPoints(),
                    m.getQuantity(),
                    m.getImage()
            ));

            return new ApiResponseDto<>(true, "Filtered merchandise fetched successfully", dtoPage);
        }




        @Override
        public ApiResponseDto<Void> softDeleteMerchandise(Long id) {
            Merchandise merchandise = merchandiseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchandise not found"));

            if (merchandise.getIsActive() == Account_Status.IN_ACTIVE) {
                throw  new ResourceNotFoundException("Merchandise already deleted");
            }

            merchandise.setIsActive(Account_Status.IN_ACTIVE);
            merchandiseRepository.save(merchandise);

            return new ApiResponseDto<>(true, "Merchandise deleted successfully ", null);
        }

        @Override
        public ApiResponseDto<MerchandiseResponseDto> updateMerchandise(Long id, AddMerchandiseDto dto, MultipartFile imageFile) throws Exception {
            Merchandise merchandise = merchandiseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchandise not found"));

            // Update basic fields
            merchandise.setName(dto.getName());
            merchandise.setRewardPoints(dto.getRequiredPoints());
            merchandise.setQuantity(dto.getAvailableQuantity());

            // Optional image update
            if (imageFile != null && !imageFile.isEmpty()) {
                Cloudinary cloudinary = cloudinaryConfig.cloudinary();
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("folder", "merchandise"));
                String imageUrl = (String) uploadResult.get("secure_url");
                merchandise.setImage(imageUrl);
            }

            Merchandise updated = merchandiseRepository.save(merchandise);

            MerchandiseResponseDto response = new MerchandiseResponseDto(
                    updated.getId(),
                    updated.getName(),
                    updated.getRewardPoints(),
                    updated.getQuantity(),
                    updated.getImage()
            );

            return new ApiResponseDto<>(true, "Merchandise updated successfully", response);
        }
        @Override
        public ApiResponseDto<MerchandiseResponseDto> restockMerchandise(Long id, Integer additionalQuantity) {
            // Validate logged-in user
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email);

            if (user == null) {
                throw  new UserNotFoundException( "User not found");
            }

            if (!(user.getRole().equals(com.project.supply.chain.management.constants.Role.OWNER) ||
                    user.getRole().equals(com.project.supply.chain.management.constants.Role.CENTRAL_OFFICE))) {
                throw  new UnauthorizedAccessException( "Access denied: Only OWNER and CENTRAL OFFICE can restock merchandise");
            }

            Merchandise merchandise = merchandiseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchandise not found"));

            if (merchandise.getIsActive() == Account_Status.IN_ACTIVE) {
                throw  new ResourceNotFoundException( "Cannot restock inactive merchandise");
            }

            merchandise.setQuantity(merchandise.getQuantity() + additionalQuantity);
            merchandiseRepository.save(merchandise);

            MerchandiseResponseDto response = new MerchandiseResponseDto(
                    merchandise.getId(),
                    merchandise.getName(),
                    merchandise.getRewardPoints(),
                    merchandise.getQuantity(),
                    merchandise.getImage()
            );

            return new ApiResponseDto<>(true, "Merchandise restocked successfully", response);
        }



    }


