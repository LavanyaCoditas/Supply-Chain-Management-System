package com.project.supply.chain.management.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tool_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(min = 2, max = 20,message = "tool category name between 2- 20 charcter ")
    private String name;

    @Column
    @Size(min = 2, max = 200, message = "tool category discription between 2-200 characters ")
    private String description;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "category")
    private List<Tool> tools;
}

