package com.app.retail.service;

import com.app.retail.model.ApprovalQueueProduct;
import com.app.retail.model.Product;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface IProductService {

     List<Product> getActiveProducts();

     List<Product> searchProducts(String productName, String minPrice, String maxPrice, String minPostedDate, String maxPostedDate) throws ParseException;

     Product addProduct(Product product);

     Product updateProduct(Integer productId, Product product);

     Product deleteProduct(Integer productId);

     List<ApprovalQueueProduct> approvalQueueProducts();

     ApprovalQueueProduct approveProduct(Integer approvalId);

     ApprovalQueueProduct rejectProduct(Integer approvaId);
}
