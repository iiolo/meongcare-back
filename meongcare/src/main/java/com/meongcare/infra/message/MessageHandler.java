package com.meongcare.infra.message;

import com.meongcare.domain.notifciation.domain.dto.FcmNotificationDTO;

public interface MessageHandler {

    void sendMessage(FcmNotificationDTO fcmNotificationDTO);


}
