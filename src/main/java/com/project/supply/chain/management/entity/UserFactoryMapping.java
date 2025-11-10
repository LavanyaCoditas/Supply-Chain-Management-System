package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_factory_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})


public class UserFactoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private  Long id;

    @ManyToOne
//    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "factory_id")
    private Factory factory;


    @ManyToOne
    @JoinColumn(name = "bay_id")
    private Bay bayId;

    @Enumerated(EnumType.STRING)
    @Column(name = "assigned_role")
    private Role assignedRole;
}
