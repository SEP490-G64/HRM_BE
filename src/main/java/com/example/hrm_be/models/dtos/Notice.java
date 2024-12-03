package com.example.hrm_be.models.dtos;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice implements Serializable {
  /** Subject notification on firebase */
  private String subject;

  /** Content notification on firebase */
  private String content;

  /** url ảnh đại diện đơn hàng */
  private String image;

  /** Map các data */
  private Map<String, String> data;

  /** FCM registration token */
  private List<String> registrationTokens;
}
