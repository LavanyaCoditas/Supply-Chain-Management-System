package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "storage_area")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @OneToMany(mappedBy = "storageArea", cascade = CascadeType.ALL)
    private List<ToolStorageMapping> toolStorageMappings;

    @Column
    @Positive
    private Integer rowNum;

    @Column
    @Positive
    private Integer colNum;

    @Column
    @Positive
    private Integer stack;

    @Column
    private String bucket;
    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

}
