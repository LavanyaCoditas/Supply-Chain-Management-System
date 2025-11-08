package com.project.supply.chain.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_central_office_mapping")
@Data @NoArgsConstructor
@AllArgsConstructor

    public class UserCentralOfficeMapping {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne @JoinColumn(name = "office_id")
        private CentralOffice office;
    }


