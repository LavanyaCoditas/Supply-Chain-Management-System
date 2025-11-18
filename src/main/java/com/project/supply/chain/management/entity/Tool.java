package com.project.supply.chain.management.entity;

        import com.project.supply.chain.management.constants.Account_Status;
        import com.project.supply.chain.management.constants.Expensive;
        import com.project.supply.chain.management.constants.ToolType;
        import jakarta.persistence.*;
        import jakarta.validation.constraints.Positive;
        import jakarta.validation.constraints.Size;
        import lombok.*;
        import java.time.LocalDateTime;
        import java.util.ArrayList;
        import java.util.List;

@Entity
@Table(name = "tool")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(min = 3,max = 50, message = "name of tool must be between 3 to 50 character")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ToolCategory category;

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ToolType type;

    @Enumerated(EnumType.STRING)
    @Column
    private Expensive isExpensive;

    @Column
    @Positive(message = "threshold must be positive")
    private Integer threshold;

    @Enumerated(EnumType.STRING)
    @Column
    private Account_Status isActive ;

    @OneToMany
    private List<ToolStock> toolStockList =new ArrayList<>();

    @Column
    private  LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;

}

