package com.project.supply.chain.management.entity;

        import com.project.supply.chain.management.constants.Account_Status;
        import com.project.supply.chain.management.constants.Expensive;
        import com.project.supply.chain.management.constants.ToolType;
        import jakarta.persistence.*;
        import lombok.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "tool")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ToolCategory category;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ToolType type;

    @Enumerated(EnumType.STRING)
    private Expensive isExpensive;

    private Integer threshold;
    private Integer qty;
    @Enumerated(EnumType.STRING)
    private Account_Status isActive ;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();


}

