package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.entity.Bay;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {

    public static Specification<UserFactoryMapping> belongsToFactory(Factory factory) {
        return (root, query, cb) -> cb.equal(root.get("factory"), factory);
    }

    public static Specification<UserFactoryMapping> hasRole(Role role) {
        return (root, query, cb) -> {
            if (role == null) return cb.conjunction();
            return cb.equal(root.get("assignedRole"), role);
        };
    }

    public static Specification<UserFactoryMapping> searchByKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String like = "%" + keyword.toLowerCase() + "%";

            Join<UserFactoryMapping, User> userJoin = root.join("user", JoinType.LEFT);
            Join<UserFactoryMapping, Bay> bayJoin = root.join("bayId", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(userJoin.get("username")), like),
                    cb.like(cb.lower(bayJoin.get("name")), like)
            );
        };
    }
}
