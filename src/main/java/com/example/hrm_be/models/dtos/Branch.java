package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.entities.CommonEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Branch {
  Long id;
  String branchName;

  BranchType branchType;

  String location;

  String contactPerson;

  String phoneNumber;

  Integer capacity;

  Boolean activeStatus;

  List<BranchBatch> branchBatches; // 1-N with BranchBatch

  List<Inbound> inbounds; // 1-N with Inbound

  List<BranchProduct> branchProducts; // 1-N with BranchProduct

  List<Outbound> outbounds; // 1-N with Outbound

  List<InventoryCheck> inventoryChecks; // 1-N with InventoryCheck

  List<User> users;

  // Setter cho id với kiểm tra
  public Branch setId(String idStr) {
    if (idStr == null) {
      this.id = null; // Gán giá trị null nếu chuỗi truyền vào là null
      return this; // Kết thúc phương thức
    }

    if (!isNumeric(idStr)) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }

    // Chuyển đổi và kiểm tra giá trị id
    this.id = Long.parseLong(idStr); // Gán giá trị đã được kiểm tra
    return this;
  }

  // Setter cho capacity với kiểm tra
  public Branch setCapacity(String capacityStr) {
    if (capacityStr == null) {
      this.capacity = null; // Gán giá trị null nếu chuỗi truyền vào là null
      return this; // Kết thúc phương thức
    }

    if (!isNumeric(capacityStr)) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }

    // Chuyển đổi và kiểm tra giá trị capacity
    this.capacity = Integer.parseInt(capacityStr); // Gán giá trị đã được kiểm tra
    return this;
  }

  public Branch setBranchType(String branchTypeStr) {
    try {
      // Gọi phương thức parse để kiểm tra giá trị
      this.branchType = BranchType.parse(branchTypeStr);
    } catch (IllegalArgumentException e) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }
    return this;
  }

  public Branch setActiveStatus(String activeStatusStr) {
    if (activeStatusStr == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }

    if (!activeStatusStr.equalsIgnoreCase("true") && !activeStatusStr.equalsIgnoreCase("false")) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID); // Ném lỗi nếu không hợp lệ
    }

    this.activeStatus = Boolean.parseBoolean(activeStatusStr);
    return this;
  }
}
