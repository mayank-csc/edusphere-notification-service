package com.edusphere.notification.event.consumer;

import com.edusphere.notification.event.SagaEvent;
import com.edusphere.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "${edusphere.kafka.topics.notification}",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(SagaEvent event) {
        log.info("Received notification event: type={} sagaId={}", event.getEventType(), event.getSagaId());

        if (SagaEvent.NOTIFICATION_REQUESTED.equals(event.getEventType())) {
            notificationService.handleNotificationRequest(event);
        } else {
            log.debug("Ignoring event type={}", event.getEventType());
        }
    }
}
