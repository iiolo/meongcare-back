package com.meongcare.domain.notifciation.domain.dto;

import com.meongcare.domain.notifciation.domain.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
public class FcmNotificationDTO {
    private String title;
    private String body;
    private String fcmToken;
    private NotificationType notificationType;
    private Long memberId;
    private Long dogId;
    private Map<String, String> data;

    public static FcmNotificationDTO of(
            String title, String body, String fcmToken,
            NotificationType notificationType, Long memberId, Long dogId
    ) {
        return new FcmNotificationDTO(
                title,
                body,
                fcmToken,
                notificationType,
                memberId,
                dogId,
                Collections.emptyMap()
        );
    }

    public static FcmNotificationDTO of(
            String title, String body, String fcmToken,
            NotificationType notificationType, Long memberId, Long dogId,
            Map<String, String> data
    ) {
        return new FcmNotificationDTO(
                title,
                body,
                fcmToken,
                notificationType,
                memberId,
                dogId,
                data
        );
    }
}
