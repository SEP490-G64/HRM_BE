package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.UnitConversionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UnitConversionRepository extends JpaRepository<UnitConversionEntity, Long> {
  List<UnitConversionEntity> getByProductId(long productId);

  @Modifying
  @Transactional
  @Query("UPDATE UnitConversionEntity u SET u.product.id = :productId WHERE u.id IN :ids")
  void assignToProductByProductIdAndIds(
      @Param("productId") Long productId, @Param("ids") List<Long> ids);
}
