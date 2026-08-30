package com.edusphere.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:noreply@edusphere.io}")
    private String fromAddress;

    public void sendWelcomeEmail(String toEmail, String recipientName,
                                  String institutionName, String subdomain,
                                  String username) {
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipientName);
            context.setVariable("institutionName", institutionName);
            context.setVariable("subdomain", subdomain);
            context.setVariable("username", username);
            context.setVariable("loginUrl", "https://" + subdomain + ".edusphere.io/login");

            String htmlContent = templateEngine.process("welcome-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to EduSphere — Your School Platform is Ready!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
}
