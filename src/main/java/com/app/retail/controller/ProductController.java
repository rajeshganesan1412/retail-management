package com.app.retail.controller;

import com.app.retail.model.ApprovalQueueProduct;
import com.app.retail.model.Product;
import com.app.retail.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
public class ProductController {

    @Autowired
    IProductService productService;


    @GetMapping("/api/products")
    public List<Product> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @GetMapping("/api/products/search")
    public List<Product> searchProducts(@RequestParam(value = "productName")String productName,@RequestParam(value = "minPrice")String minPrice,@RequestParam(value = "maxPrice") String maxPrice,@RequestParam(value = "minPostedDate") String minPostedDate,@RequestParam(value = "maxPostedDate") String maxPostedDate) throws ParseException {
        return productService.searchProducts(productName, minPrice, maxPrice, minPostedDate, maxPostedDate);
    }

    @PostMapping("/api/products")
    public Product addProduct(@Valid @RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/api/products/{productId}")
    public Product updateProduct(@PathVariable Integer productId, @RequestBody Product product) {
        return productService.updateProduct(productId, product);
    }

    @DeleteMapping("/api/products/{productId}")
    public Product deleteProduct(@PathVariable Integer productId) {
        return productService.deleteProduct(productId);
    }

    @GetMapping("/api/products/approval-queue")
    public List<ApprovalQueueProduct> getApprovalQueueProducts() {
        return productService.approvalQueueProducts();
    }

    @PutMapping("/api/products/approval-queue/{approvalId}/approve")
    public ApprovalQueueProduct approveProduct(@PathVariable Integer approvalId) {
        return productService.approveProduct(approvalId);
    }

    @PutMapping("/api/products/approval-queue/{approvalId}/reject")
    public ApprovalQueueProduct rejectProduct(@PathVariable Integer approvalId) {
        return productService.rejectProduct(approvalId);
    }
}
