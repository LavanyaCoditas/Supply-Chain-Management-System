package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.constants.ToolOrProductRequestStatus;
import com.project.supply.chain.management.entity.ToolRequest;
import com.project.supply.chain.management.entity.ToolRequestItem;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ToolRequestSpecifications {

    // ✅ Filter by status
    public static Specification<ToolRequest> hasStatus(ToolOrProductRequestStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    // ✅ Search by worker name
    public static Specification<ToolRequest> searchByWorkerName(String workerName) {
        return (root, query, cb) -> {
            if (workerName == null || workerName.isBlank()) return cb.conjunction();
            Join<Object, Object> workerJoin = root.join("worker", JoinType.LEFT);
            return cb.like(cb.lower(workerJoin.get("username")), "%" + workerName.toLowerCase() + "%");
        };
    }

    // ✅ Search by tool name (via ToolRequestItem → Tool)
    public static Specification<ToolRequest> searchByToolName(String toolName) {
        return (root, query, cb) -> {
            if (toolName == null || toolName.isBlank()) return cb.conjunction();
            Join<Object, ToolRequestItem> itemJoin = root.join("toolItems", JoinType.LEFT);
            Join<Object, Object> toolJoin = itemJoin.join("tool", JoinType.LEFT);
            query.distinct(true);
            return cb.like(cb.lower(toolJoin.get("name")), "%" + toolName.toLowerCase() + "%");
        };
    }

    // ✅ Filter by "isExpensive" using toolItems → tool
    public static Specification<ToolRequest> isExpensive(boolean isPlantHead) {
        return (root, query, cb) -> {
            Join<Object, Object> itemJoin = root.join("toolItems", JoinType.LEFT);
            Join<Object, Object> toolJoin = itemJoin.join("tool", JoinType.LEFT);
            query.distinct(true);

            if (isPlantHead) {
                // Plant Head should see only expensive tools (YES)
                return cb.equal(toolJoin.get("isExpensive"), com.project.supply.chain.management.constants.Expensive.YES);
            } else {
                // Chief Supervisor should see only non-expensive tools (NO)
                return cb.equal(toolJoin.get("isExpensive"), com.project.supply.chain.management.constants.Expensive.NO);
            }
        };
    }


}
