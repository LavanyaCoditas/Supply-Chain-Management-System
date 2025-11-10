package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.Account_Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private Long quantity;
    private Long rewardPoints;
    private LocalDateTime createdAt;
    private Account_Status isActive;
}
