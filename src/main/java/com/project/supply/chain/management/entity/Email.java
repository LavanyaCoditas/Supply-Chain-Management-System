package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String recipient;
    private String subject;

    @Column(columnDefinition = "text")
    private String body;
}
