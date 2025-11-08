package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.entity.Factory;
import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class FactorySpecifications {

    public static Specification<Factory> searchFactories(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction(); // no filtering if no keyword
            }

            String like = "%" + keyword.toLowerCase() + "%";

            // Join plant head for searching by their name/email
            Join<Object, Object> plantHeadJoin = root.join("planthead", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("city")), like),
                    cb.like(cb.lower(root.get("address")), like),
                    cb.like(cb.lower(plantHeadJoin.get("username")), like),
                    cb.like(cb.lower(plantHeadJoin.get("email")), like)
            );

        };

    }
    public static Specification<Factory> isActiveFilter(Account_Status status) {
        return (root, query, cb) -> cb.equal(root.get("isActive").as(String.class), status.name());
    }
}
