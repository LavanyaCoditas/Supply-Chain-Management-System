package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class ProductSpecifications {

    public static Specification<Product> searchProducts(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction(); // no filtering if keyword empty
            }

            String likePattern = "%" + keyword.toLowerCase() + "%";

            // Join category for search by categoryName
            Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("name")), likePattern),
                    cb.like(cb.lower(root.get("prodDescription")), likePattern),
                    cb.like(cb.lower(categoryJoin.get("categoryName")), likePattern)
            );
        };
    }

    public static Specification<Product> hasCategoryName(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null || categoryName.isBlank()) {
                return cb.conjunction();
            }
            Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);
            return cb.like(cb.lower(categoryJoin.get("categoryName")), "%" + categoryName.toLowerCase() + "%");
        };
    }
}
