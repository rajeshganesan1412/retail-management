package com.app.retail.repository;

import com.app.retail.model.ApprovalQueueProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IApprovalQueueRepository extends JpaRepository<ApprovalQueueProduct, Integer> {
}
