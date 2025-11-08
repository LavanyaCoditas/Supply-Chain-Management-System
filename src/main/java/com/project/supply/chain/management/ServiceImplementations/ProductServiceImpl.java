package com.project.supply.chain.management.ServiceImplementations;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.supply.chain.management.Repositories.ProductCategoryRepository;
import com.project.supply.chain.management.Repositories.ProductRepository;
import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.ServiceInterfaces.ProductService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.AddProductDto;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.ProductResponseDto;
import com.project.supply.chain.management.entity.Product;
import com.project.supply.chain.management.entity.ProductCategory;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.specifications.ProductSpecifications;
import com.project.supply.chain.management.util.CloudinaryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository categoryRepository;
@Autowired
    CloudinaryConfig cloudinaryConfig;

@Autowired
    UserRepository userRepository;

    @Override
    public ApiResponse<ProductResponseDto> uploadProductWithImage(AddProductDto productDto, MultipartFile imageFile) {
        try {
            Cloudinary cloudinary = cloudinaryConfig.cloudinary();

            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                    ObjectUtils.asMap("folder", "products"));

            String imageUrl = (String) uploadResult.get("secure_url");

            ProductCategory category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Product product = new Product();
            product.setName(productDto.getName());
            product.setProdDescription(productDto.getProdDescription());
            product.setPrice(productDto.getPrice());
            product.setRewardPts(productDto.getRewardPts());
            product.setCategory(category);
            product.setImage(imageUrl);

            Product savedProduct = productRepository.save(product);
            ProductResponseDto responseDto= new ProductResponseDto
                    (savedProduct.getId(),
                    savedProduct.getName(),
                            savedProduct.getProdDescription(),
                            savedProduct.getPrice(),savedProduct.getRewardPts()
            ,savedProduct.getCategory().getCategoryName(),savedProduct.getImage(),savedProduct.getIsActive());

            return new ApiResponse<>(true, "Product uploaded successfully", responseDto);
        } catch (IOException e) {
            return new ApiResponse<>(false, "Image upload failed: " + e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error uploading product: " + e.getMessage(), null);
        }
    }
    @Override
    public ApiResponse<Page<ProductResponseDto>> getAllProducts(int page, int size, String search, String categoryName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Start with an "empty" Specification
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        // Dynamically combine specifications
        if (search != null && !search.isBlank()) {
            spec = spec.and(ProductSpecifications.searchProducts(search));
        }
        if (categoryName != null && !categoryName.isBlank()) {
            spec = spec.and(ProductSpecifications.hasCategoryName(categoryName));
        }

        Page<Product> products = productRepository.findAll(spec, pageable);

        Page<ProductResponseDto> dtoPage = products.map(product -> {
            ProductResponseDto dto = new ProductResponseDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setProdDescription(product.getProdDescription());
            dto.setPrice(product.getPrice());
            dto.setRewardPts(product.getRewardPts());
            dto.setCategoryName(product.getCategory().getCategoryName());
            dto.setImageUrl(product.getImage());
            dto.setIsActive(product.getIsActive());
            return dto;
        });

        return new ApiResponse<>(true, "Products fetched successfully", dtoPage);
    }

    @Override
    public ApiResponse<String> softDeleteProduct(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User loggedInUser = userRepository.findByEmail(username);

                if(loggedInUser ==null && loggedInUser.getRole()!= Role.OWNER)
                {
                    throw new RuntimeException("User Not found");
                }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getIsActive() == Account_Status.IN_ACTIVE) {
            return new ApiResponse<>(false, "Product already inactive", null);
        }
        product.setIsActive(Account_Status.IN_ACTIVE);
        productRepository.save(product);
        return new ApiResponse<>(true, "Product marked as IN_ACTIVE successfully", "Product id : " + productId);
    }

}
