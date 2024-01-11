package com.objects.marketbridge.domain.order.repository;

import com.objects.marketbridge.domain.order.domain.ProdOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<ProdOrder, Long> {
}
