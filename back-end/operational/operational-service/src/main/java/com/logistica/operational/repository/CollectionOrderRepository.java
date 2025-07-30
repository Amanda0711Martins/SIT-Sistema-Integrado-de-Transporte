package com.logistica.operational.repository;

import com.logistica.operational.models.CollectionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CollectionOrderRepository extends JpaRepository<CollectionOrder, Long> {
    List<CollectionOrder> findByCustomerId(Long customerId);
    List<CollectionOrder> findByStatus(CollectionOrder.OrderStatus status);
}