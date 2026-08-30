package com.edusphere.notification.event.producer;

import com.edusphere.notification.event.SagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, SagaEvent> kafkaTemplate;

    @Value("${edusphere.kafka.topics.notification}")
    private String notificationTopic;

    public void publishNotificationSent(String sagaId, Map<String, Object> payload) {
        publish(sagaId, SagaEvent.NOTIFICATION_SENT, payload);
    }

    public void publishNotificationFailed(String sagaId, Map<String, Object> payload) {
        publish(sagaId, SagaEvent.NOTIFICATION_FAILED, payload);
    }

    private void publish(String sagaId, String eventType, Map<String, Object> payload) {
        SagaEvent event = SagaEvent.of(sagaId, eventType, payload);
        kafkaTemplate.send(notificationTopic, sagaId, event);
        log.info("Published {} for saga={}", eventType, sagaId);
    }
}
