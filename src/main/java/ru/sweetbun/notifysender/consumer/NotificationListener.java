package ru.sweetbun.notifysender.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.sweetbun.notifysender.model.Notification;
import ru.sweetbun.notifysender.service.EmailService;

@RequiredArgsConstructor
@Component
public class NotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = "notificationQueue")
    public void processNotification(Notification notification) {
        emailService.sendEmail(notification);
    }
}
