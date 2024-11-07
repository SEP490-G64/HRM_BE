package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.services.BatchService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BatchServiceImpl implements BatchService {
  // Injects the repository to interact with batch data in the database
  @Autowired private BatchRepository batchRepository;

  // Injects the mapper to convert between DTO and Entity objects for batches
  @Autowired private BatchMapper batchMapper;

  // Retrieves a Batch by its ID
  @Override
  public Batch getById(Long id) {
    // Validation: Check if the ID is null
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.INVALID);
    }

    return Optional.ofNullable(id)
        .flatMap(e -> batchRepository.findById(e).map(b -> batchMapper.convertToDtoBasicInfo(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of Batch entities, allowing sorting and searching by name
  @Override
  public Page<Batch> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      Long productId,
      String keyword,
      LocalDateTime produceStartDate,
      LocalDateTime produceEndDate,
      LocalDateTime expireStartDate,
      LocalDateTime expireEndDate) {
    if (pageNo < 0 || pageSize < 1) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if (sortBy == null) {
      sortBy = "id";
    }
    if (!Objects.equals(sortBy, "id")
        && !Objects.equals(sortBy, "batchCode")
        && !Objects.equals(sortBy, "produceDate")
        && !Objects.equals(sortBy, "expireDate")
        && !Objects.equals(sortBy, "inboundPrice")) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if ((produceStartDate != null && produceEndDate != null)
        && produceStartDate.isAfter(produceEndDate)) {
      throw new HrmCommonException(HrmConstant.ERROR.DATE.INVALID_RANGE);
    }

    if ((expireStartDate != null && expireEndDate != null)
        && expireStartDate.isAfter(expireEndDate)) {
      throw new HrmCommonException(HrmConstant.ERROR.DATE.INVALID_RANGE);
    }

    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return batchRepository
        .findAll(
            getSpecification(
                productId,
                keyword,
                produceStartDate,
                produceEndDate,
                expireStartDate,
                expireEndDate),
            pageable)
        .map(dao -> batchMapper.convertToDtoBasicInfo(dao));
  }

  private Specification<BatchEntity> getSpecification(
      Long productId,
      String keyword,
      LocalDateTime produceStartDate,
      LocalDateTime produceEndDate,
      LocalDateTime expireStartDate,
      LocalDateTime expireEndDate) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Điều kiện productId
      predicates.add(criteriaBuilder.equal(root.get("product").get("id"), productId));

      // Điều kiện batchCode chứa keyword
      if (keyword != null && !keyword.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("batchCode"), "%" + keyword + "%"));
      }

      // Điều kiện về produceDate
      if (produceStartDate != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(root.get("produceDate"), produceStartDate));
      }
      if (produceEndDate != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("produceDate"), produceEndDate));
      }

      // Điều kiện về expireDate
      if (expireStartDate != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(root.get("expireDate"), expireStartDate));
      }
      if (expireEndDate != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expireDate"), expireEndDate));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  // Creates a new Batch
  @Override
  public Batch create(Batch batch) {
    if (batch == null || !commonValidate(batch)) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.INVALID);
    }

    // Validation: Ensure the Batch does not already exist at the same batch code
    if (batchRepository.existsByBatchCode(batch.getBatchCode())) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(batch)
        .map(e -> batchMapper.toEntity(e))
        .map(e -> batchRepository.save(e))
        .map(e -> batchMapper.toDTO(e))
        .orElse(null);
  }

  // Updates an existing Batch
  @Override
  public Batch update(Batch batch) {
    if (batch == null || !commonValidate(batch)) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.INVALID);
    }

    // Retrieve the existing Batch entity by ID
    BatchEntity oldBatchEntity = batchRepository.findById(batch.getId()).orElse(null);
    if (oldBatchEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.NOT_EXIST);
    }

    if (batchRepository.existsByBatchCode(batch.getBatchCode())
        && !Objects.equals(batch.getBatchCode(), oldBatchEntity.getBatchCode())) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.EXIST);
    }

    // Update the fields of the existing Batch entity with new values
    return Optional.ofNullable(oldBatchEntity)
        .map(
            op ->
                op.toBuilder()
                    .batchCode(batch.getBatchCode())
                    .expireDate(batch.getExpireDate())
                    .produceDate(batch.getProduceDate())
                    .inboundPrice(batch.getInboundPrice())
                    .build())
        .map(batchRepository::save)
        .map(batchMapper::toDTO)
        .orElse(null);
  }

  // Deletes a Batch by ID
  @Override
  public void delete(Long id) {
    // Validation: Check if the ID is null
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.INVALID);
    }

    // Delete the batch by ID
    batchRepository.deleteById(id);
  }

  @Override
  public Batch addBatchInInbound(Batch batch, Product product) {
  Batch saved =  batchRepository.findByBatchCodeAndProduct_Id(batch.getBatchCode(),
          product.getId()).map(batchMapper::toDTO)
        .orElseGet(
            () -> batchMapper.toDTO(batchRepository.save(batchMapper.toEntity(batch))));
return saved;
  }

  @Override
  public List<BatchEntity> findAllByProductId(Long inboundId) {
    return batchRepository.findAllByProductId(inboundId);
  }

  // This method will validate category field input values
  private boolean commonValidate(Batch batch) {
    if (batch.getBatchCode() == null
        || batch.getBatchCode().trim().isEmpty()
        || batch.getBatchCode().length() > 100) {
      return false;
    }

    LocalDateTime produceDate = batch.getProduceDate();
    LocalDateTime earliestProduceDate =
        LocalDateTime.of(2000, 1, 1, 0, 0); // Adjust as reasonable earliest date
    if (produceDate != null) {
      if (produceDate.isBefore(earliestProduceDate) || produceDate.isAfter(LocalDateTime.now())) {
        return false;
      }
    }

    // Validate expireDate (required, must be after produceDate and within a reasonable range)
    LocalDateTime expireDate = batch.getExpireDate();
    LocalDateTime maxExpireDate =
        LocalDateTime.now().plusYears(10); // Adjust as reasonable max expire date
    if (expireDate == null) {
      return false;
    } else if (produceDate != null && expireDate.isBefore(produceDate)) {
      return false;
    } else if (expireDate.isAfter(maxExpireDate)) {
      return false;
    }

    BigDecimal inboundPrice = batch.getInboundPrice();
    if (inboundPrice != null) {
      if (inboundPrice.compareTo(BigDecimal.ZERO) <= 0
          || inboundPrice.compareTo(new BigDecimal("10000000")) >= 0) {
        return false;
      }
    }

    return true;
  }
}
