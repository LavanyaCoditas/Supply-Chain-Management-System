package com.project.supply.chain.management.ServiceImplementations;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.supply.chain.management.Repositories.*;
import com.project.supply.chain.management.ServiceInterfaces.ProductService;
import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.dto.*;
import com.project.supply.chain.management.entity.*;
import com.project.supply.chain.management.exceptions.ResourceAlreadyExistsException;
import com.project.supply.chain.management.exceptions.ResourceNotFoundException;
import com.project.supply.chain.management.exceptions.UserNotFoundException;
import com.project.supply.chain.management.specifications.ProductSpecifications;
import com.project.supply.chain.management.util.ApplicationUtils;
import com.project.supply.chain.management.util.CloudinaryConfig;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
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
@AllArgsConstructor
public class ProductServiceImpl implements ProductService
{
    private final ApplicationUtils appUtils;

    private final ProductRepository productRepository;

    private final ProductCategoryRepository categoryRepository;

    private final CloudinaryConfig cloudinaryConfig;


    @Override
    public ApiResponseDto<ProductResponseDto> uploadProduct(AddProductDto productDto, MultipartFile imageFile) throws IOException {

        Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(productDto.getName());
        if (existingProduct.isPresent()) {
            throw new ResourceAlreadyExistsException("A product with this name already exists: " + productDto.getName());
        }

        Cloudinary cloudinary = cloudinaryConfig.cloudinary();
        Map uploadResult = cloudinary.uploader().upload(
                imageFile.getBytes(),
                ObjectUtils.asMap("folder", "products")
        );
        String imageUrl = (String) uploadResult.get("secure_url");


        ProductCategory category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));


        Product product = new Product();
        product.setName(productDto.getName().trim());
        product.setProdDescription(productDto.getProdDescription());
        product.setPrice(productDto.getPrice());
        product.setRewardPts(productDto.getRewardPts());
        product.setCategory(category);
        product.setThreshold(productDto.getThreshold());
        product.setImage(imageUrl);
        product.setIsActive(Account_Status.ACTIVE);

        Product savedProduct = productRepository.save(product);

        ProductResponseDto responseDto = new ProductResponseDto(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getProdDescription(),
                savedProduct.getPrice(),
                savedProduct.getRewardPts(),
                savedProduct.getCategory().getCategoryName(),
                savedProduct.getThreshold(),
                savedProduct.getImage(),
                savedProduct.getIsActive()
        );

        return new ApiResponseDto<>(true, "Product uploaded successfully", responseDto);
    }

    @Override
    public ApiResponseDto<Page<ProductResponseDto>> getAllProducts(int page, int size, String search, String categoryName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // include only active products
        Specification<Product> spec = (root, query, cb) -> cb.equal(root.get("isActive"), Account_Status.ACTIVE);


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
            dto.setThreshold(product.getThreshold());
            return dto;
        });

        return new ApiResponseDto<>(true, "Active products fetched successfully", dtoPage);
    }


    @Override
    public ApiResponseDto<String> softDeleteProduct(Long productId) {
       User loggedInUser=appUtils.getUser(appUtils.getLoggedInUserEmail());

                if(loggedInUser ==null && loggedInUser.getRole()!= Role.OWNER)
                {
                    throw new UserNotFoundException("User Not found");
                }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getIsActive() == Account_Status.IN_ACTIVE) {
            return new ApiResponseDto<>(false, "Product already inactive", null);
        }
        product.setIsActive(Account_Status.IN_ACTIVE);
        productRepository.save(product);
        return new ApiResponseDto<>(true, "Product marked as IN_ACTIVE successfully", "Product id : " + productId);
    }

    @Override
    public ApiResponseDto<ProductResponseDto> updateProduct(Long id, AddProductDto productDto, MultipartFile imageFile) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));


        if (productDto.getCategoryId() != null) {
            ProductCategory category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }


        if (productDto.getName() != null && !productDto.getName().isBlank()) product.setName(productDto.getName());
        if (productDto.getProdDescription() != null) product.setProdDescription(productDto.getProdDescription());
        if (productDto.getPrice() != null) product.setPrice(productDto.getPrice());
        if (productDto.getRewardPts() != null) product.setRewardPts(productDto.getRewardPts());
        if (productDto.getThreshold() != null) product.setThreshold(productDto.getThreshold());

        if (imageFile != null && !imageFile.isEmpty()) {
            Cloudinary cloudinary = cloudinaryConfig.cloudinary();
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                    ObjectUtils.asMap("folder", "products"));
            product.setImage((String) uploadResult.get("secure_url"));
        }

        productRepository.save(product);

        ProductResponseDto dto = new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getProdDescription(),
                product.getPrice(),
                product.getRewardPts(),
                product.getCategory().getCategoryName(),
                product.getThreshold(),
                product.getImage(),
                product.getIsActive()
        );

        return new ApiResponseDto<>(true, "Product updated successfully", dto);
    }


}
