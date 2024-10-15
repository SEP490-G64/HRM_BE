package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.SpecialConditionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SpecialConditionRepository extends JpaRepository<SpecialConditionEntity, Long> {
  @Modifying
  @Transactional
  @Query("UPDATE SpecialConditionEntity p SET p.product.id = :productId WHERE p.id IN :ids")
  void assignToProductByProductIdAndIds(
      @Param("productId") Long productId, @Param("ids") List<Long> ids);
}
