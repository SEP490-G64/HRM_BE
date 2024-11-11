package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UnitConversionRepository extends JpaRepository<UnitConversionEntity, Long> {
  List<UnitConversionEntity> getByProductId(long productId);

  @Modifying
  @Transactional
  @Query("UPDATE UnitConversionEntity u SET u.product.id = :productId WHERE u.id IN :ids")
  void assignToProductByProductIdAndIds(
      @Param("productId") Long productId, @Param("ids") List<Long> ids);

  List<UnitConversionEntity> findByProductAndLargerUnit(
      ProductEntity product, UnitOfMeasurementEntity unit);

  Optional<UnitConversionEntity> findByProductIdAndLargerUnitIdAndSmallerUnitId(
      Long productId, Long largerUnitId, Long smallerUnitId);
}
