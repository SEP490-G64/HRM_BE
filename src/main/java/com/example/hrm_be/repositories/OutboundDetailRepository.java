package com.example.hrm_be.repositories;

import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundDetailRepository extends JpaRepository<OutboundDetailEntity, Long> {

  Optional<OutboundDetailEntity> findByOutboundAndBatch(OutboundEntity outbound, BatchEntity batch);
  List<OutboundDetailEntity> findByBatchAndOutbound(BatchEntity batch, OutboundEntity outbound);

}
