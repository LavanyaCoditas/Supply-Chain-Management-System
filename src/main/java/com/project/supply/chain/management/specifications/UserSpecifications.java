package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<User> withFilters(String search, String role, Long factoryId) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();


            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("username")), pattern));
            }

            if (role != null && !role.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("role").as(String.class), role));
            }

            if (factoryId != null) {

                Join<Object, Object> factoryJoin = root.join("factoryMappings", JoinType.LEFT);
                predicate = cb.and(predicate, cb.equal(factoryJoin.get("factory").get("id"), factoryId));
            }

            return predicate;
        };
    }
}
