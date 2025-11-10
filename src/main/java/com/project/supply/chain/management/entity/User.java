package com.project.supply.chain.management.entity;

import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.constants.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor
@ToString(exclude = {"factoryMappings"})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String img;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Long phone;
    private String password;
    @Enumerated(EnumType.STRING)
    private Account_Status isActive =Account_Status.ACTIVE;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user")
    private List<UserFactoryMapping> factoryMappings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

}
