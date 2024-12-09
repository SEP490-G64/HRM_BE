package com.example.hrm_be.controllers;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebsocketController {
  private final SimpMessagingTemplate messagingTemplate;

  public WebsocketController(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/inventory-check/{inventoryCheckId}")
  public void handleMessage(@DestinationVariable String inventoryCheckId, @Payload String message) {
    log.info("Received message for inventoryCheckId " + inventoryCheckId + ": " + message);
    // Prepare JSON response
    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Update received for inventoryCheckId: " + inventoryCheckId);
    response.put("timestamp", System.currentTimeMillis());
    // Handle the message here
    messagingTemplate.convertAndSend("/topic/inventory-check/" + inventoryCheckId, response);
  }
}
