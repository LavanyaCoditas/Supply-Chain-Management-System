
    package com.project.supply.chain.management.ServiceImplementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.supply.chain.management.Repositories.MerchandiseRepository;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.specifications.MerchandiseSpecifications;
import com.project.supply.chain.management.util.CloudinaryConfig;
import com.project.supply.chain.management.dto.AddMerchandiseDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.MerchandiseResponseDto;
import com.project.supply.chain.management.entity.Merchandise;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.ServiceInterfaces.MerchandiseService;
import com.project.supply.chain.management.Repositories.UserRepository;
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
    public class MerchandiseServiceImpl implements MerchandiseService {

        private final MerchandiseRepository merchandiseRepository;
        private final CloudinaryConfig cloudinaryConfig;
        private final UserRepository userRepository;

        public MerchandiseServiceImpl(MerchandiseRepository merchandiseRepository,
                                      CloudinaryConfig cloudinaryConfig,
                                      UserRepository userRepository) {
            this.merchandiseRepository = merchandiseRepository;
            this.cloudinaryConfig = cloudinaryConfig;
            this.userRepository = userRepository;
        }

        @Override
        public ApiResponse<MerchandiseResponseDto> addMerchandise(AddMerchandiseDto dto, MultipartFile image) throws IOException {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            if (merchandiseRepository.existsByNameIgnoreCase(dto.getName())) {
                return new ApiResponse<>(false, "Merchandise with this name already exists", null);
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

            return new ApiResponse<>(true, "Merchandise added successfully", response);
        }



            @Override

            public ApiResponse<Page<MerchandiseResponseDto>> getAllMerchandise(int page, int size, String search, String sort) {
                Pageable pageable;

                // ✅ Sorting logic
                if ("rewardPointsAsc".equalsIgnoreCase(sort)) {
                    pageable = PageRequest.of(page, size, Sort.by("rewardPoints").ascending());
                } else if ("rewardPointsDesc".equalsIgnoreCase(sort)) {
                    pageable = PageRequest.of(page, size, Sort.by("rewardPoints").descending());
                } else {
                    pageable = PageRequest.of(page, size, Sort.by("id").descending());
                }

                // ✅ Initialize spec
                Specification<Merchandise> spec = (root, query, cb) -> cb.conjunction();

                // ✅ Filter by active status
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("isActive"), Account_Status.ACTIVE)
                );

                // ✅ Optional search filter
                if (search != null && !search.isBlank()) {
                    spec = spec.and(MerchandiseSpecifications.searchByName(search));
                }

                // ✅ Query DB
                Page<Merchandise> merchandisePage = merchandiseRepository.findAll(spec, pageable);

                // ✅ Map to DTO
                Page<MerchandiseResponseDto> dtoPage = merchandisePage.map(m -> new MerchandiseResponseDto(
                        m.getId(),
                        m.getName(),
                        m.getRewardPoints(),
                        m.getQuantity(),
                        m.getImage()
                ));

                return new ApiResponse<>(true, "Active merchandise fetched successfully", dtoPage);
            }

        @Override
        public ApiResponse<Void> softDeleteMerchandise(Long id) {
            Merchandise merchandise = merchandiseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Merchandise not found"));

            if (merchandise.getIsActive() == Account_Status.IN_ACTIVE) {
                return new ApiResponse<>(false, "Merchandise already deleted", null);
            }

            merchandise.setIsActive(Account_Status.IN_ACTIVE);
            merchandiseRepository.save(merchandise);

            return new ApiResponse<>(true, "Merchandise deleted successfully ", null);
        }

        @Override
        public ApiResponse<MerchandiseResponseDto> updateMerchandise(Long id, AddMerchandiseDto dto, MultipartFile imageFile) throws Exception {
            Merchandise merchandise = merchandiseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Merchandise not found"));

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

            return new ApiResponse<>(true, "Merchandise updated successfully", response);
        }
        @Override
        public ApiResponse<MerchandiseResponseDto> restockMerchandise(Long id, Long additionalQuantity) {
            // 🔐 Validate logged-in user
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            if (!(user.getRole().equals(com.project.supply.chain.management.constants.Role.OWNER) ||
                    user.getRole().equals(com.project.supply.chain.management.constants.Role.CENTRAL_OFFICE))) {
                return new ApiResponse<>(false, "Access denied: Only OWNER or CENTRAL_OFFICE can restock merchandise", null);
            }

            // ✅ Find merchandise
            Merchandise merchandise = merchandiseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Merchandise not found"));

            if (merchandise.getIsActive() == Account_Status.IN_ACTIVE) {
                return new ApiResponse<>(false, "Cannot restock inactive merchandise", null);
            }

            // ✅ Update quantity
            merchandise.setQuantity(merchandise.getQuantity() + additionalQuantity);
//            merchandise.setUpdatedAt(LocalDateTime.now());
            merchandiseRepository.save(merchandise);

            // ✅ Build response
            MerchandiseResponseDto response = new MerchandiseResponseDto(
                    merchandise.getId(),
                    merchandise.getName(),
                    merchandise.getRewardPoints(),
                    merchandise.getQuantity(),
                    merchandise.getImage()
            );

            return new ApiResponse<>(true, "Merchandise restocked successfully", response);
        }



    }


