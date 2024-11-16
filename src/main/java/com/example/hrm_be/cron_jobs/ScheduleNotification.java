package com.example.hrm_be.cron_jobs;

import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.repositories.BranchProductRepository;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.BranchProductService;
import com.example.hrm_be.services.NotificationService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class ScheduleNotification {
  @Autowired private BranchProductService branchProductService;
  @Autowired private BatchService batchService;
  @Autowired private NotificationService notificationService;
  @Autowired private BranchProductRepository branchProductRepository;

  @Autowired private BranchRepository branchRepository;
  private static final long EXPIRED_IN_MILLISECOND = 1800000L;

  // Scheduled task that runs every 24 hours
  @Scheduled(fixedRate = EXPIRED_IN_MILLISECOND)
  public void sendDailyNotifications() {
    // Get all branches
    List<BranchEntity> branches = branchRepository.findAll();

    // Loop through each branch and send notifications
    for (BranchEntity branch : branches) {
      log.info("đã gửi thông báo cho chi nhánh với id:" + branch.getId());
      Long branchId = branch.getId();
      notificationService.createAlertProductNotification(branchId);
    }
  }
}
