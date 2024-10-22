package com.example.hrm_be.utils;

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
    mailSender.setPassword("acmt nape xjip palj");

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }

  public SimpleMailMessage constructResetTokenEmail(String fullPath, String token, String userEmail) {
    String message = "Vui lòng nhấp vào liên kết dưới đây để đặt lại mật khẩu của bạn cho Ứng dụng Quản lý kho Hệ thống nhà thuốc:\n" +
            fullPath + "\n\n" +
            "Sau khi nhấp vào liên kết, bạn sẽ được hướng dẫn để thiết lập mật khẩu mới. " +
            "Xin lưu ý rằng liên kết này sẽ hết hạn sau 1 giờ để đảm bảo an toàn cho tài khoản của bạn.\n" +
            "Nếu bạn không yêu cầu thay đổi mật khẩu, hãy bỏ qua email này.\n\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!";

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(userEmail);
    email.setSubject("Đặt lại mật khẩu tài khoản Ứng dụng Quản lý kho Hệ thống nhà thuốc");
    email.setText(message);

    return email;
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
