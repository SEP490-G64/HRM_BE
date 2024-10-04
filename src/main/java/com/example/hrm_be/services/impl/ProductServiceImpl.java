package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.ProductService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
  @Autowired
  private ProductRepository productRepository;

  @Autowired private ProductMapper productMapper;

  @Override
  public Product getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> productRepository.findById(e).map(b -> productMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Product> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductEntitiesByProductNameContainingIgnoreCase(name, pageable)
        .map(dao -> productMapper.toDTO(dao));
  }

  @Override
  public Product create(Product product) {
    if (product == null) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }
    return Optional.ofNullable(product)
        .map(e -> productMapper.toEntity(e))
        .map(e -> productRepository.save(e))
        .map(e -> productMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public Product update(Product product) {
    ProductEntity oldProductEntity = productRepository.findById(product.getId()).orElse(null);
    if (oldProductEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_EXIST);
    }
    return Optional.ofNullable(oldProductEntity)
        .map(
            op ->
                op.toBuilder()
                    .productName(product.getProductName())
                    .status(product.getStatus())
                    .build())
        .map(productRepository::save)
        .map(productMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null) {
      return;
    }
    productRepository.deleteById(id);
  }

  @Override
  public Page<Product> getByPagingAndCateId(int pageNo, int pageSize, String sortBy, Long cateId) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductByPagingAndCategoryId(cateId, pageable)
        .map(dao -> productMapper.toDTO(dao));
  }

  @Override
  public Page<Product> getByPagingAndTypeId(int pageNo, int pageSize, String sortBy, Long TypeId) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductByPagingAndTypeId(TypeId, pageable)
        .map(dao -> productMapper.toDTO(dao));
  }
}
