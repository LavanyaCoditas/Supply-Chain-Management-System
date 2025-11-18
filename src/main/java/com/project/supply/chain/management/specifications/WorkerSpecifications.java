package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import com.project.supply.chain.management.entity.Bay;
import com.project.supply.chain.management.entity.Factory;
import com.project.supply.chain.management.entity.User;
import com.project.supply.chain.management.entity.UserFactoryMapping;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class WorkerSpecifications {

    //Search workers by name (case-insensitive)
    public static Specification<UserFactoryMapping> searchByWorkerName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();
            Join<UserFactoryMapping, User> userJoin = root.join("user", JoinType.LEFT);
            return cb.like(cb.lower(userJoin.get("username")), "%" + name.toLowerCase() + "%");
        };
    }

     // Search by Factory Name

    public static Specification<UserFactoryMapping> searchByFactoryName(String factoryName) {
        return (root, query, cb) -> {
            if (factoryName == null || factoryName.isBlank()) return cb.conjunction();
            Join<UserFactoryMapping, Factory> factoryJoin = root.join("factory", JoinType.LEFT);
            return cb.like(cb.lower(factoryJoin.get("factoryName")), "%" + factoryName.toLowerCase() + "%");
        };
    }


     // Search by Bay Name

    public static Specification<UserFactoryMapping> searchByBayName(String bayName) {
        return (root, query, cb) -> {
            if (bayName == null || bayName.isBlank()) return cb.conjunction();
            Join<UserFactoryMapping, Bay> bayJoin = root.join("bay", JoinType.LEFT);
            return cb.like(cb.lower(bayJoin.get("bayName")), "%" + bayName.toLowerCase() + "%");
        };
    }


     // Filter by Factory entity directly

    public static Specification<UserFactoryMapping> belongsToFactory(Factory factory) {
        return (root, query, cb) -> {
            if (factory == null) return cb.conjunction();
            return cb.equal(root.get("factory"), factory);
        };
    }


     // Filter by Bay entity directly

    public static Specification<UserFactoryMapping> belongsToBay(Bay bay) {
        return (root, query, cb) -> {
            if (bay == null) return cb.conjunction();
            return cb.equal(root.get("bay"), bay);
        };
    }


     // Filter by Role
    public static Specification<UserFactoryMapping> hasRole(Role role) {
        return (root, query, cb) -> {
            if (role == null) return cb.conjunction();
            return cb.equal(root.get("assignedRole"), role);
        };
    }

     // Filter by account status
    public static Specification<UserFactoryMapping> hasAccountStatus(Account_Status status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            Join<UserFactoryMapping, User> userJoin = root.join("user", JoinType.LEFT);
            return cb.equal(userJoin.get("isActive"), status);
        };
    }
}
