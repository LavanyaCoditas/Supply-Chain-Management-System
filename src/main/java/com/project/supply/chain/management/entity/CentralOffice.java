package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "central_office")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentralOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long centralOfficeId;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "centralOffice", fetch = FetchType.LAZY)
    private List<Factory> factories;

    @OneToMany(mappedBy = "office", fetch = FetchType.LAZY)
    private List<UserCentralOfficeMapping> userMappings;
}

