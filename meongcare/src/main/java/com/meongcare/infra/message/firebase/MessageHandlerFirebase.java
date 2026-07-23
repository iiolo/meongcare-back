package com.meongcare.infra.message.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.meongcare.domain.member.domain.entity.Member;
import com.meongcare.domain.member.domain.repository.MemberRepository;
import com.meongcare.domain.notifciation.domain.dto.FcmNotificationDTO;
import com.meongcare.domain.notifciation.domain.entity.NotificationRecord;
import com.meongcare.domain.notifciation.domain.repository.NotificationRecordRepository;
import com.meongcare.infra.message.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageHandlerFirebase implements MessageHandler {

    private static final String DATA_KEY_NOTIFICATION_TYPE = "notificationType";
    private static final String DATA_KEY_TITLE = "title";
    private static final String DATA_KEY_BODY = "body";
    private static final String DATA_KEY_DOG_ID = "dogId";
    private static final String DATA_KEY_LOGO_IMAGE_URL = "logoImageUrl";
    private static final String EMPTY_LOGO_IMAGE_URL = "";

    private final FirebaseMessaging firebaseMessaging;
    private final NotificationRecordRepository notificationRecordRepository;
    private final MemberRepository memberRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = FirebaseMessagingException.class)
    @Override
    public void sendMessage(FcmNotificationDTO fcmNotificationDTO) {
        String fcmToken = fcmNotificationDTO.getFcmToken();
        if (fcmToken == null) {
            return;
        }

        Message message = createDataMessage(fcmNotificationDTO);

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.warn("[{}] {} (memberId={}, fcmToken={})", e.getClass().getSimpleName(), e.getMessage(), fcmNotificationDTO.getMemberId(), fcmToken);
            Member member = memberRepository.getMember(fcmNotificationDTO.getMemberId());
            member.deleteFcmToken();
        } catch (Exception e) {
            log.warn("[{}] {} (memberId={}, fcmToken={})", e.getClass().getSimpleName(), e.getMessage(), fcmNotificationDTO.getMemberId(), fcmToken);
            notificationRecordRepository.save(new NotificationRecord(
                    fcmNotificationDTO.getNotificationType(), fcmNotificationDTO.getMemberId(), fcmNotificationDTO.getDogId(),
                    fcmNotificationDTO.getTitle(), fcmNotificationDTO.getBody()
            ));
        }
    }

    private Message createDataMessage(FcmNotificationDTO fcmNotificationDTO) {
        Message.Builder messageBuilder = Message.builder()
                .setToken(fcmNotificationDTO.getFcmToken())
                .putData(DATA_KEY_NOTIFICATION_TYPE, fcmNotificationDTO.getNotificationType().name())
                .putData(DATA_KEY_TITLE, fcmNotificationDTO.getTitle())
                .putData(DATA_KEY_BODY, fcmNotificationDTO.getBody())
                .putData(DATA_KEY_LOGO_IMAGE_URL, EMPTY_LOGO_IMAGE_URL)
                .putAllData(fcmNotificationDTO.getData());
        if (fcmNotificationDTO.getDogId() != null) {
            messageBuilder.putData(DATA_KEY_DOG_ID, String.valueOf(fcmNotificationDTO.getDogId()));
        }
        return messageBuilder.build();
    }
}
