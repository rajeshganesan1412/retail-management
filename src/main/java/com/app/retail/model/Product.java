package com.app.retail.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Integer productId;
    @NotBlank(message = "Product name required")
    private String productName;
    @DecimalMax(value = "10000", message = "Price value should not be more than 10,000")
    private BigDecimal price;
    private Status status;
    private Date datePosted;
}
