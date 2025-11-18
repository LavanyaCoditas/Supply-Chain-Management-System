package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.entity.CentralOfficeInventory;
import com.project.supply.chain.management.entity.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CentralOfficeInventorySpecifications {

    public static Specification<CentralOfficeInventory> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) return null;
            Join<CentralOfficeInventory, Product> productJoin = root.join("product");
            return criteriaBuilder.equal(productJoin.get("id"), productId);
        };
    }

    public static Specification<CentralOfficeInventory> withProductName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.trim().isEmpty()) return null;
            Join<CentralOfficeInventory, Product> productJoin = root.join("product");
            return criteriaBuilder.like(
                    criteriaBuilder.lower(productJoin.get("name")),
                    "%" + productName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<CentralOfficeInventory> withMinQuantity(Long minQuantity) {
        return (root, query, criteriaBuilder) -> {
            if (minQuantity == null) return null;
            return criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), minQuantity);
        };
    }

    public static Specification<CentralOfficeInventory> withMaxQuantity(Long maxQuantity) {
        return (root, query, criteriaBuilder) -> {
            if (maxQuantity == null) return null;
            return criteriaBuilder.lessThanOrEqualTo(root.get("quantity"), maxQuantity);
        };
    }

    public static Specification<CentralOfficeInventory> withFilters(
            Long productId,
            String productName,
            Long minQuantity,
            Long maxQuantity) {
        return Specification.allOf(
                withProductId(productId),
                withProductName(productName),
                withMinQuantity(minQuantity),
                withMaxQuantity(maxQuantity)
        );
    }
}