package com.app.retail.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Getter
@Setter
@Entity
public class ApprovalQueueProduct implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Integer approvalId;
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Status status;
    private ApprovalStatus approvalStatus;
    private Date approvalRequestDate;
}
