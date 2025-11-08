package com.project.supply.chain.management.Repositories;

import com.project.supply.chain.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product>
{

}
