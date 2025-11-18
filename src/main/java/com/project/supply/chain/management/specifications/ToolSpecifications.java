package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.entity.Tool;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ToolSpecifications {

    //  Search by tool name
    public static Specification<Tool> searchByName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();
            query.distinct(true);
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    //  Search by category name
    public static Specification<Tool> searchByCategory(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null || categoryName.isBlank()) return cb.conjunction();
            Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);
            query.distinct(true);
            return cb.like(cb.lower(categoryJoin.get("name")), "%" + categoryName.toLowerCase() + "%");
        };
    }

    //  Search by tool type
    public static Specification<Tool> searchByType(String type) {
        return (root, query, cb) -> {
            if (type == null || type.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("type")), type.toLowerCase());
        };
    }

    //  Filter by expensive flag (YES/NO)
    public static Specification<Tool> filterByExpensive(String isExpensive) {
        return (root, query, cb) -> {
            if (isExpensive == null || isExpensive.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("isExpensive")), isExpensive.toLowerCase());
        };
    }

    //  Filter by stock availability
    public static Specification<Tool> filterByStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null) return cb.conjunction();
            if (inStock)
                return cb.greaterThan(root.get("availableQuantity"), 0);
            else
                return cb.lessThanOrEqualTo(root.get("availableQuantity"), 0);
        };
    }
}
