package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<User> withFilters(String search, String role, Long factoryId) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            // Prevent duplicate rows when joining
            query.distinct(true);

            // Join with UserFactoryMapping (userFactoryMappingRepository.findByUser(user) proves mapping exists)
            Join<User, UserFactoryMapping> mappingJoin = root.join("factoryMappings", JoinType.LEFT);

            // Search by username
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("username")), pattern)
                );
            }

            // Filter by role
            if (role != null && !role.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("role"), role)
                );
            }

            // Filter by factory ID
            if (factoryId != null) {
                predicate = cb.and(predicate,
                        cb.equal(mappingJoin.get("factory").get("id"), factoryId)
                );
            }

            return predicate;
        };
    }
}
