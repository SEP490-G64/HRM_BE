package com.example.hrm_be.configs.inits;


import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.services.RoleService;
import com.example.hrm_be.services.UserRoleMapService;
import com.example.hrm_be.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class CommonInitializer implements ApplicationRunner {

  @Autowired private RoleService roleService;
  @Autowired private UserService userService;
  @Autowired private UserRoleMapService userRoleMapService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    this.initRoles();
    this.initAdminUser();
  }

  private void initRoles() {

    Role userRole = roleService.getStaffRole();
    if (userRole == null) {
      userRole = roleService.createStaffRole();
    }
    log.info("Created role: {}", userRole.getType());

    Role managerRole = roleService.getManagerRole();
    if (managerRole == null) {
      managerRole = roleService.createManagerRole();
    }
    log.info("Created role: {}", managerRole.getType());

    Role adminRole = roleService.getAdminRole();
    if (adminRole == null) {
      adminRole = roleService.createAdminRole();
    }
    log.info("Created role: {}", adminRole.getType());
  }

  private void initAdminUser() {

    User oldAdminUser = userService.findLoggedInfoByEmail("dsdadmin@gmail.com");

    if (oldAdminUser != null
        && !oldAdminUser.getRoles().isEmpty()
        && oldAdminUser.getRoles().stream()
            .anyMatch(role -> role.getType() != null && role.getType().isManager())
        && oldAdminUser.getRoles().stream()
            .anyMatch(role -> role.getType() != null && role.getType().isAdmin())) {
      log.info("Admin exists");
      return;
    }

    User newAdminUser =
        User.builder()
            .email("dsdadmin@gmail.com")
            .password("Abcd1234")
            .userName("dsdadmin")
            .phone("")
            .build();
    User createdAdminUser = userService.createAdmin(newAdminUser);
    log.info(
        "Created admin user with id: {}, email: {}, username: {}",
        createdAdminUser.getId(),
        createdAdminUser.getEmail(),
        createdAdminUser.getUserName());
  }
}
