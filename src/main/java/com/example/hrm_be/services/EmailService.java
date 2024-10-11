package com.example.hrm_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired private JavaMailSender mailSender;

  public void sendEmail(String to, String subject, String text) {
    // Create simple mail
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("tiendat288966@gmail.com");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    // Send email
    mailSender.send(message);
  }
}
