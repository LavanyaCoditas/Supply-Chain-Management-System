package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long>
{

    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
