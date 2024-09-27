package com.example.hrm_be.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailUtil {
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    mailSender.setUsername("duongcdhe176312@gmail.com");
    mailSender.setPassword("ahihidongoc123");

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }

  public SimpleMailMessage constructResetTokenEmail(
      String contextPath, String token, String userEmail) {
    String url = contextPath + "/user/changePassword?token=" + token;
    String message = "Please click on the link for reset your password:";
    return constructEmail("Reset Password", message + " \r\n" + url, userEmail);
  }

  private SimpleMailMessage constructEmail(String subject, String body, String userEmail) {
    SimpleMailMessage email = new SimpleMailMessage();
    email.setSubject(subject);
    email.setText(body);
    email.setTo(userEmail);
    email.setFrom("longtamsupport@gmail.com");
    return email;
  }
}
