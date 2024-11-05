package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.InventoryCheckDetailsEntity;
import com.example.hrm_be.models.entities.InventoryCheckProductDetailsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryCheckProductDetailsRepository   extends
    JpaRepository<InventoryCheckProductDetailsEntity, Long> {
  Optional<InventoryCheckProductDetailsEntity> findByInventoryCheck_IdAndProduct_Id(Long checkId,
      Long productId);

  void deleteAllByInventoryCheck_Id(Long checkId);
}
