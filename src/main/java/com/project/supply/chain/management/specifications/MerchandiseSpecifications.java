package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.entity.Merchandise;
import org.springframework.data.jpa.domain.Specification;

public class MerchandiseSpecifications {

    // Search by merchandise name
    public static Specification<Merchandise> searchByName(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    // Filter reward points >= min
    public static Specification<Merchandise> hasMinRewardPoints(Integer minPoints) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("rewardPoints"), minPoints);
    }

    // Filter reward points <= max
    public static Specification<Merchandise> hasMaxRewardPoints(Integer maxPoints) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("rewardPoints"), maxPoints);
    }
}
