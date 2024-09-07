package com.app.retail.service;

import com.app.retail.exception.ActiveProductsNotFoundException;
import com.app.retail.exception.ApprovalQueueNotFoundException;
import com.app.retail.exception.PriceLimitExceedException;
import com.app.retail.exception.ProductNotFoundException;
import com.app.retail.model.ApprovalQueueProduct;
import com.app.retail.model.ApprovalStatus;
import com.app.retail.model.Product;
import com.app.retail.model.Status;
import com.app.retail.repository.IApprovalQueueRepository;
import com.app.retail.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements IProductService{

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IApprovalQueueRepository approvalQueueRepository;

    /**
     * API to List Active Products
     *
     * @return List<Product>
     */
    @Override
    public List<Product> getActiveProducts() {
        List<Product> productList = productRepository.findAll();
        if(!productList.isEmpty()) {
            return productList.stream().filter(product -> product.getStatus().equals(Status.ACTIVE))
                    .sorted(Comparator.comparing(Product::getDatePosted).reversed())
                    .collect(Collectors.collectingAndThen(Collectors.toList(), products -> {
                        if (products.isEmpty()) throw new ActiveProductsNotFoundException();
                        return products;
                    }));
        } else {
            throw new ProductNotFoundException();
        }
    }

    /**
     * API to Search Products
     *
     * @param productName
     * @param minPrice
     * @param maxPrice
     * @param minPostedDate
     * @param maxPostedDate
     * @return List<Product>
     */
    @Override
    public List<Product> searchProducts(String productName, String minPrice, String maxPrice, String minPostedDate, String maxPostedDate) throws ParseException {
        if(productName != null && !productName.equals("null")) {
            List<Product> productList = productRepository.findByProductName(productName);
            if(!productList.isEmpty()) {
                return productList;
            } else {
                throw new ProductNotFoundException();
            }
        } else if (minPrice != null && maxPrice != null && !minPrice.equals("null") && !maxPrice.equals("null")) {
            return productRepository.findByPriceBetween(new BigDecimal(minPrice), new BigDecimal(maxPrice));
        } else if (minPostedDate != null && maxPostedDate != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date minPostedDateValue = df.parse(minPostedDate);
            Date maxPostedDateValue = df.parse(maxPostedDate);
            return productRepository.findByDatePostedBetween(minPostedDateValue, maxPostedDateValue);
        } else {
            throw new ProductNotFoundException();
        }
    }

    /**
     * API to Create a Product
     *
     * @param product
     * @return Product
     */
    @Override
    public Product addProduct(Product product) {
        if(product.getPrice().compareTo(BigDecimal.valueOf(5000)) == 1) {
             ApprovalQueueProduct approvalQueueProduct = new ApprovalQueueProduct();
            Random random = new Random();
            approvalQueueProduct.setProductId(random.nextInt(900) + 100);
             approvalQueueProduct.setProductName(product.getProductName());
             approvalQueueProduct.setPrice(product.getPrice());
             approvalQueueProduct.setStatus(Status.NON_ACTIVE);
             approvalQueueProduct.setApprovalStatus(ApprovalStatus.NOT_APPROVED);
             approvalQueueProduct.setApprovalRequestDate(new Date());
             approvalQueueRepository.save(approvalQueueProduct);
             return new Product();
        }
        else {
            Product newProduct = new Product();
            newProduct.setProductName(product.getProductName());
            newProduct.setPrice(product.getPrice());
            newProduct.setStatus(product.getPrice().doubleValue() < 5000 ? product.getStatus() : Status.NON_ACTIVE);
            newProduct.setDatePosted(new Date());
            return productRepository.save(newProduct);
        }
    }

    /**
     * API to Update a Product
     *
     * @param productId
     * @param product
     * @return Product
     */
    @Override
    public Product updateProduct(Integer productId,Product product) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isPresent()) {
            double percentage = (product.getPrice().doubleValue() - optionalProduct.get().getPrice().doubleValue()) * 100 / optionalProduct.get().getPrice().doubleValue();
            Product newProduct = new Product();
            if (product.getPrice().compareTo(BigDecimal.valueOf(10000)) == 1){
                    throw new PriceLimitExceedException();
            } else {
                if (percentage > 50) {
                    newProduct.setProductId(optionalProduct.get().getProductId());
                    newProduct.setProductName(product.getProductName());
                    newProduct.setPrice(product.getPrice());
                    newProduct.setStatus(Status.NON_ACTIVE);
                    newProduct.setDatePosted(optionalProduct.get().getDatePosted());

                    ApprovalQueueProduct approvalQueueProduct = new ApprovalQueueProduct();
                    approvalQueueProduct.setProductId(newProduct.getProductId());
                    approvalQueueProduct.setProductName(newProduct.getProductName());
                    approvalQueueProduct.setPrice(newProduct.getPrice());
                    approvalQueueProduct.setStatus(newProduct.getStatus());
                    approvalQueueProduct.setApprovalStatus(ApprovalStatus.NOT_APPROVED);
                    approvalQueueProduct.setApprovalRequestDate(new Date());
                    productRepository.deleteById(productId);
                    approvalQueueRepository.save(approvalQueueProduct);
                    return newProduct;
                } else {
                    newProduct.setProductId(optionalProduct.get().getProductId());
                    newProduct.setProductName(product.getProductName());
                    newProduct.setPrice(product.getPrice());
                    newProduct.setStatus(product.getStatus());
                    newProduct.setDatePosted(optionalProduct.get().getDatePosted());
                    return productRepository.save(newProduct);
                }
            }
        } else {
            throw new ProductNotFoundException();
        }
    }

    /**
     * API to Delete a Product
     *
     * @param productId
     * @return Product
     */
    @Override
    public Product deleteProduct(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        productRepository.deleteById(productId);
        if(optionalProduct.isPresent()) {
            ApprovalQueueProduct approvalQueueProduct = new ApprovalQueueProduct();
            approvalQueueProduct.setProductId(optionalProduct.get().getProductId());
            approvalQueueProduct.setProductName(optionalProduct.get().getProductName());
            approvalQueueProduct.setPrice(optionalProduct.get().getPrice());
            approvalQueueProduct.setStatus(Status.NON_ACTIVE);
            approvalQueueProduct.setApprovalStatus(ApprovalStatus.NOT_APPROVED);
            approvalQueueProduct.setApprovalRequestDate(new Date());
            approvalQueueRepository.save(approvalQueueProduct);
            return optionalProduct.get();
        }
        else {
            throw new ProductNotFoundException();
        }
    }

    /**
     * API to Get Products in Approval Queue
     *
     * @return List<ApprovalQueueProduct>
     */
    @Override
    public List<ApprovalQueueProduct> approvalQueueProducts() {
        List<ApprovalQueueProduct> approvalQueueProductList = approvalQueueRepository.findAll();
        if(approvalQueueProductList != null) {
            return approvalQueueProductList.stream().sorted(Comparator.comparing(ApprovalQueueProduct::getApprovalRequestDate).reversed()).collect(Collectors.toList());
        } else {
            throw  new ApprovalQueueNotFoundException();
        }
    }

    /**
     * API to Approve a Product
     *
     * @param approvalId
     * @return ApprovalQueueProduct
     */
    @Override
    public ApprovalQueueProduct approveProduct(Integer approvalId) {
        Optional<ApprovalQueueProduct> approvalQueueProduct = approvalQueueRepository.findById(approvalId);
        if(approvalQueueProduct.isPresent()) {
                Product product = new Product();
                product.setProductId(approvalQueueProduct.get().getProductId());
                product.setProductName(approvalQueueProduct.get().getProductName());
                product.setPrice(approvalQueueProduct.get().getPrice());
                product.setStatus(Status.ACTIVE);
                product.setDatePosted(new Date());
                productRepository.save(product);
                approvalQueueRepository.deleteById(approvalId);
                approvalQueueProduct.get().setApprovalStatus(ApprovalStatus.APPROVED);
                approvalQueueProduct.get().setStatus(product.getStatus());
                return approvalQueueProduct.get();
        } else {
            throw new ApprovalQueueNotFoundException();
        }
    }

    /**
     * API to Reject a Product
     *
     * @param approvalId
     * @return ApprovalQueueProduct
     */
    @Override
    public ApprovalQueueProduct rejectProduct(Integer approvalId) {
        Optional<ApprovalQueueProduct> approvalQueueProduct = approvalQueueRepository.findById(approvalId);
       if(approvalQueueProduct.isPresent()) {
           approvalQueueRepository.deleteById(approvalId);
           approvalQueueProduct.get().setApprovalStatus(ApprovalStatus.REJECTED);
           return approvalQueueProduct.get();
       } else {
           throw new ApprovalQueueNotFoundException();
       }

    }
}
