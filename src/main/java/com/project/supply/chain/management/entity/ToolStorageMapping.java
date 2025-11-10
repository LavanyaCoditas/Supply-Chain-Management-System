package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
        import lombok.*;
        import java.time.Instant;

@Entity
@Table(name = "tool_storage_mapping")
@Data @NoArgsConstructor @AllArgsConstructor
public class ToolStorageMapping {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne

    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne

    @JoinColumn(name = "tool_id")
    private Tool tool;

    @ManyToOne

    @JoinColumn(name = "storage_area_id")
    private StorageArea storageArea;

    private Instant assignedAt = Instant.now();
}
