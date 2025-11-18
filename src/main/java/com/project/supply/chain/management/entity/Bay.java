package com.project.supply.chain.management.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "bay")
@Data @NoArgsConstructor @AllArgsConstructor
public class Bay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @NotBlank(message = "Bay name cannot be blank")
    @Size(min = 1, max = 100, message = "Bay name must be between 1-100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(updatable = true,nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}

