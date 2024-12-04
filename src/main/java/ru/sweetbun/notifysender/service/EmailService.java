package ru.sweetbun.notifysender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.sweetbun.notifysender.model.Notification;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    private final ModelMapper modelMapper;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    public void sendEmail(Notification notification) {
        try {
            isValidNotification(notification);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        modelMapper.map(notification, message);
        mailSender.send(message);
        log.info("Sending notification to mail: {}", notification.getTo());
    }

    private void isValidNotification(Notification notification) {
        if (notification == null)
            throw new IllegalArgumentException("Notification cannot be null");
        if (!isValidEmail(notification.getTo()))
            throw new IllegalArgumentException("Invalid email format: " + notification.getTo());
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
