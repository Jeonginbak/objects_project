package com.objects.marketbridge.product.infra;

import com.objects.marketbridge.product.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {
}
