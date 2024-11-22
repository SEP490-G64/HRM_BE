package com.example.hrm_be.common.utils;

import com.example.hrm_be.services.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EmailServiceTest {
  @Mock private JavaMailSender mailSender; // Mock the JavaMailSender

  @InjectMocks private EmailService emailService; // Inject mocks into EmailService

  public EmailServiceTest() {
    MockitoAnnotations.openMocks(this); // Initialize mocks
  }

  @Test
  void testSendEmail() {
    // Arrange
    String to = "test@example.com";
    String subject = "Test Subject";
    String text = "Test email content";

    // Create a SimpleMailMessage object to verify
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("tiendat288966@gmail.com");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    // Act
    emailService.sendEmail(to, subject, text);

    // Assert
    verify(mailSender, times(1)).send(refEq(message)); // Verify mailSender.send was called
  }
}
