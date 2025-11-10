package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne @JoinColumn(name = "tool_id")
    private Tool tool;

    @OneToMany(mappedBy = "storageArea", cascade = CascadeType.ALL)
    private List<ToolStorageMapping> toolStorageMappings;

    private Integer rowNum;
    private Integer colNum;
    private Integer stack;
    private Integer bucket;

}
