package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.StockBatchReport;
import com.example.hrm_be.models.dtos.StockProductReport;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ReportMapper {
    public StockProductReport convertToStockProductReport(BranchProductEntity entity) {
        return StockProductReport.builder()
                .productId(entity.getId())
                .image(entity.getProduct().getUrlImage())
                .registrationCode(entity.getProduct().getRegistrationCode())
                .productName(entity.getProduct().getProductName())
                .minQuantity(entity.getMinQuantity())
                .maxQuantity(entity.getMaxQuantity())
                .totalQuantity(entity.getQuantity())
                .sellableQuantity(entity.getQuantity())
                .storageLocation(entity.getStorageLocation() != null ? entity.getStorageLocation().getShelfName() : "Chưa có thông tin")
                .unit(entity.getProduct().getBaseUnit().getUnitName())
                .batches(new ArrayList<>()) // Khởi tạo danh sách batch
                .build();
    }

    public StockBatchReport convertToStockBatchReport(BranchBatchEntity entity) {
        return StockBatchReport.builder()
                .batchId(entity.getBatch().getId())
                .batchCode(entity.getBatch().getBatchCode())
                .expireDate(entity.getBatch().getExpireDate())
                .totalQuantity(entity.getQuantity())
                .build();
    }
}
