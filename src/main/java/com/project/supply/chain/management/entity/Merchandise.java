package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "merchandise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Merchandise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String image;

    private Integer rewardPoints;
}
