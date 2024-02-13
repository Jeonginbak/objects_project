package com.objects.marketbridge.product.infra.option;

import com.objects.marketbridge.product.domain.ProdOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdOptionJpaRepository extends JpaRepository<ProdOption, Long> {
}