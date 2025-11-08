package com.project.supply.chain.management.entity;


import jakarta.persistence.*;
        import lombok.*;
        import java.util.List;

@Entity
@Table(name = "product_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
