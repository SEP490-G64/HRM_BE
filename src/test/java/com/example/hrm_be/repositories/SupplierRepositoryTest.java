package com.example.hrm_be.repositories;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.models.entities.SupplierEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@Transactional
class SupplierRepositoryTest {

  @Autowired private SupplierRepository supplierRepository;

  private SupplierEntity supplier;

  @BeforeEach
  void setup() {
    supplier = createTestSupplier("Test Supplier", "123 Main Street");
  }

  private SupplierEntity createTestSupplier(String name, String address) {
    SupplierEntity testSupplier = new SupplierEntity();
    testSupplier.setSupplierName(name);
    testSupplier.setAddress(address);
    return testSupplier;
  }

  @Test
  void shouldCreateSupplier() {
    SupplierEntity savedSupplier = supplierRepository.save(supplier);

    assertNotNull(savedSupplier);
    assertNotNull(savedSupplier.getId());
    assertEquals(supplier.getSupplierName(), savedSupplier.getSupplierName());
    assertEquals(supplier.getAddress(), savedSupplier.getAddress());
  }

  @Test
  void shouldListAllSuppliers() {
    supplierRepository.save(supplier);

    SupplierEntity anotherSupplier = createTestSupplier("Another Supplier", "456 Another Street");
    supplierRepository.save(anotherSupplier);

    List<SupplierEntity> suppliers = supplierRepository.findAll();

    assertFalse(suppliers.isEmpty());
    assertEquals(2, suppliers.size());
  }

  @Test
  void shouldFindSupplierById() {
    SupplierEntity savedSupplier = supplierRepository.save(supplier);

    Optional<SupplierEntity> foundSupplier = supplierRepository.findById(savedSupplier.getId());

    assertTrue(foundSupplier.isPresent());
    assertEquals(savedSupplier.getId(), foundSupplier.get().getId());
  }

  @Test
  void shouldUpdateSupplier() {
    SupplierEntity savedSupplier = supplierRepository.save(supplier);

    savedSupplier.setSupplierName("Updated Supplier");
    savedSupplier.setAddress("456 Updated Street");
    SupplierEntity updatedSupplier = supplierRepository.save(savedSupplier);

    assertEquals("Updated Supplier", updatedSupplier.getSupplierName());
    assertEquals("456 Updated Street", updatedSupplier.getAddress());
  }

  @Test
  void shouldDeleteSupplier() {
    SupplierEntity savedSupplier = supplierRepository.save(supplier);

    supplierRepository.deleteById(savedSupplier.getId());

    Optional<SupplierEntity> deletedSupplier = supplierRepository.findById(savedSupplier.getId());
    assertFalse(deletedSupplier.isPresent());
  }

  @Test
  void shouldCheckIfSupplierExistsByNameAndAddress() {
    supplierRepository.save(supplier);

    boolean exists =
        supplierRepository.existsBySupplierNameAndAddress("Test Supplier", "123 Main Street");

    assertTrue(exists);
  }

  @Test
  void shouldReturnFalseIfSupplierDoesNotExistByNameAndAddress() {
    supplierRepository.save(supplier);

    boolean exists =
        supplierRepository.existsBySupplierNameAndAddress(
            "Non-existent Supplier", "999 Non-existent Street");

    assertFalse(exists);
  }
}
