package ru.sweetbun.notifysender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.sweetbun.notifysender.model.Notification;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    private final ModelMapper modelMapper;

    public void sendEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        modelMapper.map(notification, message);
        mailSender.send(message);
        log.info("Sending notification to mail: {}", notification.getTo());
    }
}
