package com.project.supply.chain.management.specifications;

import com.project.supply.chain.management.constants.Expensive;
import com.project.supply.chain.management.entity.Tool;
import com.project.supply.chain.management.entity.ToolRequest;
import com.project.supply.chain.management.entity.ToolRequestItem;
import com.project.supply.chain.management.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ToolRequestSpecifications {

    public static Specification<ToolRequest> filterByStatus(String status) {
        return (root, query, builder) ->
                builder.equal(root.get("status"), status.toUpperCase());
    }

    public static Specification<ToolRequest> searchByToolName(String toolName) {
        return (root, query, builder) -> {
            Join<ToolRequest, ToolRequestItem> items = root.join("toolItems");
            Join<ToolRequestItem, Tool> tool = items.join("tool");

            return builder.like(builder.lower(tool.get("name")), "%" + toolName.toLowerCase() + "%");
        };
    }

    public static Specification<ToolRequest> searchByWorkerName(String name) {
        return (root, query, builder) -> {
            Join<ToolRequest, User> worker = root.join("worker");
            return builder.like(builder.lower(worker.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<ToolRequest> filterByToolType(String toolType) {
        return (root, query, builder) -> {
            Join<ToolRequest, ToolRequestItem> items = root.join("toolItems");
            Join<ToolRequestItem, Tool> tool = items.join("tool");

            return builder.equal(tool.get("type"), toolType.toUpperCase());
        };
    }

    public static Specification<ToolRequest> onlyExpensiveRequests() {
        return (root, query, builder) -> {
            Join<ToolRequest, ToolRequestItem> items = root.join("toolItems");
            Join<ToolRequestItem, Tool> tool = items.join("tool");

            return builder.equal(tool.get("isExpensive"), Expensive.YES);
        };
    }

    public static Specification<ToolRequest> onlyNormalRequests() {
        return (root, query, builder) -> {
            Join<ToolRequest, ToolRequestItem> items = root.join("toolItems");
            Join<ToolRequestItem, Tool> tool = items.join("tool");

            return builder.equal(tool.get("isExpensive"), Expensive.NO);
        };
    }

    public static Specification<ToolRequest> filterByWorker(User worker) {
        return (root, query, builder) ->
                builder.equal(root.get("worker"), worker);
    }
}
