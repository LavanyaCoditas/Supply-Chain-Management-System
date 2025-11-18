package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
        import lombok.*;
        import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "tool_storage_mapping")
@Data @NoArgsConstructor @AllArgsConstructor
public class ToolStorageMapping {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_area_id")
    private StorageArea storageArea;

    @Column
    private LocalDateTime createdAt ;
}
