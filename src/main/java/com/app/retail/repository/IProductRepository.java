package com.app.retail.repository;

import com.app.retail.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer> {

//    @Query("SELECT p FROM Product p WHERE " +
//            "p.productName LIKE CONCAT('%',:name, '%')")
    List<Product> findByProductName(String productName);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByDatePostedBetween(Date minPostedDate, Date maxPostedDate);
}
