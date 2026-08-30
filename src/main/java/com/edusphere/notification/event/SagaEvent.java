package com.edusphere.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SagaEvent {

    private String sagaId;
    private String eventType;
    private Map<String, Object> payload;
    private String timestamp;

    public static final String NOTIFICATION_REQUESTED = "NOTIFICATION_REQUESTED";
    public static final String NOTIFICATION_SENT      = "NOTIFICATION_SENT";
    public static final String NOTIFICATION_FAILED    = "NOTIFICATION_FAILED";

    public static SagaEvent of(String sagaId, String eventType, Map<String, Object> payload) {
        return SagaEvent.builder()
                .sagaId(sagaId)
                .eventType(eventType)
                .payload(payload != null ? payload : new HashMap<>())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
