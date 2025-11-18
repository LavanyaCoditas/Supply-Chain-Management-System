package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.Account_Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
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

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String image;

    @Column(nullable = false)
    @Positive(message = "quantity entered must be positive")
    private Integer quantity;

    @Column
    @Positive(message = "reward points entered must be positive")
    private Integer rewardPoints;

    @Column
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column
    private Account_Status isActive;
}
