package com.project.supply.chain.management.entity;
import com.project.supply.chain.management.constants.Account_Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "factories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String address;
    private String name;

    @ManyToOne @JoinColumn(name = "planthead_id")
    private User planthead;

    @ManyToOne @JoinColumn(name = "central_office_id")
    private CentralOffice centralOffice;

    @Column(name = "is_active")
    @Enumerated(EnumType.STRING)
    private Account_Status isActive;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "factory")
    private List<Bay> bays;

    @OneToMany(mappedBy = "factory")
    private List<StorageArea> storageAreas;

    @OneToMany(mappedBy = "factory")
    private List<ToolStorageMapping> toolStorageMappings;
}





