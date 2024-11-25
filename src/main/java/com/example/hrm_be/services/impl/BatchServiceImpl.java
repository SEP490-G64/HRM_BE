package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BatchStatus;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.BatchDto;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.UserService;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;
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
  @Autowired private UserService userService;

  // Retrieves a Batch by its ID
  @Override
  public Batch getById(Long id) {
    // Validation: Check if the ID is null
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.REQUEST.INVALID);
    }

    return Optional.ofNullable(id)
        .flatMap(e -> batchRepository.findById(e).map(b -> batchMapper.convertToDtoBasicInfo(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of Batch entities, allowing sorting and searching by name
  @Override
  public Page<BatchDto> getByPaging(
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
        .map(dao -> batchMapper.convertToDtoWithQuantity(dao));
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
    if (batchRepository
        .findByBatchCodeIgnoreCaseAndProduct_Id(batch.getBatchCode(), batch.getProduct().getId())
        .isPresent()) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.EXIST);
    }

    batch.setId(null);

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

    if (batchRepository
            .findByBatchCodeIgnoreCaseAndProduct_Id(
                batch.getBatchCode(), batch.getProduct().getId())
            .isPresent()
        && !Objects.equals(batch.getId(), oldBatchEntity.getId())) {
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
      throw new HrmCommonException(HrmConstant.ERROR.REQUEST.INVALID);
    }

    // Delete the batch by ID
    batchRepository.updateBatchStatus(BatchStatus.DA_XOA, id);
  }

  @Override
  public List<Batch> getBatchesByProductThroughInbound(Long productId) {
    return null;
  }

  @Override
  public Batch addBatchInInbound(Batch batch, Product product) {

    Optional<BatchEntity> existingBatch =
        batchRepository.findByBatchCodeIgnoreCaseAndProduct_Id(
            batch.getBatchCode(), product.getId());
    batch.setProduct(product);
    BatchEntity savedBatch =
        existingBatch.orElseGet(() -> batchRepository.save(batchMapper.toEntity(batch)));

    return batchMapper.toDTO(savedBatch);
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
      if (inboundPrice.compareTo(BigDecimal.ZERO) < 0
          || inboundPrice.compareTo(new BigDecimal("10000000")) >= 0) {
        return false;
      }
    }

    return true;
  }

  @Override
  public List<Batch> getExpiredBatches(LocalDateTime now) {
    return batchRepository.findExpiredBatches(now).stream()
        .map(batchMapper::convertToDtoBasicInfoWithProductBaseDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<Batch> getExpiredBatchesInDays(LocalDateTime now, Long days) {
    // Calculate the end date for the expiry range
    LocalDateTime expiryDate = now.plusDays(days);
    String userEmail = userService.getAuthenticatedUserEmail();
    Long branchId = userService.findBranchIdByUserEmail(userEmail).orElse(null);
    // Query the repository to find batches that expire within the specified range
    return batchRepository.findBatchesExpiringInDays(now, expiryDate, branchId).stream()
        // Map each BatchEntity to the corresponding Product object
        // Convert the Product to the ProductBaseDTO using the mapper
        .map(batchMapper::convertToDtoBasicInfoWithProductBaseDto)
        // Collect the results into a List
        .collect(Collectors.toList());
  }
}
