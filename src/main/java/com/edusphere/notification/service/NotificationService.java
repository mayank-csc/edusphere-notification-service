package com.edusphere.notification.service;

import com.edusphere.notification.domain.entity.NotificationLog;
import com.edusphere.notification.domain.repository.NotificationLogRepository;
import com.edusphere.notification.event.SagaEvent;
import com.edusphere.notification.event.producer.NotificationEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationLogRepository logRepository;
    private final EmailService emailService;
    private final NotificationEventProducer eventProducer;

    @Transactional
    public void handleNotificationRequest(SagaEvent event) {
        Map<String, Object> payload = event.getPayload();
        String sagaId        = event.getSagaId();
        String recipientEmail = (String) payload.get("recipientEmail");
        String recipientName  = (String) payload.get("recipientName");
        String institutionName = (String) payload.get("institutionName");
        String subdomain      = (String) payload.get("subdomain");
        String username       = (String) payload.get("username");

        log.info("Sending welcome email for saga={} to={}", sagaId, recipientEmail);

        NotificationLog logEntry = NotificationLog.builder()
                .sagaId(sagaId)
                .recipientEmail(recipientEmail)
                .subject("Welcome to EduSphere")
                .status("PENDING")
                .build();
        logRepository.save(logEntry);

        try {
            emailService.sendWelcomeEmail(recipientEmail, recipientName, institutionName, subdomain, username);

            logEntry.setStatus("SENT");
            logEntry.setSentAt(LocalDateTime.now());
            logRepository.save(logEntry);

            eventProducer.publishNotificationSent(sagaId, Map.of(
                    "recipientEmail", recipientEmail,
                    "notificationId", String.valueOf(logEntry.getId())
            ));

        } catch (Exception e) {
            log.error("Notification failed for saga={}: {}", sagaId, e.getMessage());
            logEntry.setStatus("FAILED");
            logEntry.setErrorMessage(e.getMessage());
            logRepository.save(logEntry);

            eventProducer.publishNotificationFailed(sagaId, Map.of(
                    "reason", e.getMessage(),
                    "recipientEmail", recipientEmail
            ));
        }
    }
}
