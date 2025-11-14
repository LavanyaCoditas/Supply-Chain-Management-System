package com.project.supply.chain.management.entity;

        import com.project.supply.chain.management.constants.Account_Status;
        import com.project.supply.chain.management.constants.Expensive;
        import com.project.supply.chain.management.constants.ToolType;
        import jakarta.persistence.*;
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

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ToolCategory category;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ToolType type;

    @Enumerated(EnumType.STRING)
    private Expensive isExpensive;

    private Integer threshold;

    @Enumerated(EnumType.STRING)
    private Account_Status isActive ;

    @OneToMany
    private List<ToolStock> toolStockList =new ArrayList<>();
    private  LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

