package ru.sweetbun.notifysender.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.sweetbun.notifysender.model.Notification;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmailService emailService;

    private Notification notification;

    @Value("${spring.mail.username}")
    private String from;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        notification = Notification.builder().to("test@example.com").subject("Test Subject").text("Test Body").build();
    }

    @Test
    void sendEmail_PositiveScenario() {
        // Arrange

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(notification.getTo());
        mailMessage.setSubject(notification.getSubject());
        mailMessage.setText(notification.getText());

        doAnswer(invocation -> {
            Notification source = invocation.getArgument(0);
            SimpleMailMessage destination = invocation.getArgument(1);
            destination.setTo(source.getTo());
            destination.setSubject(source.getSubject());
            destination.setText(source.getText());
            return null;
        }).when(modelMapper).map(any(Notification.class), any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(notification);

        // Assert
        verify(mailSender, times(1)).send(mailMessage);
        verifyNoMoreInteractions(mailSender, modelMapper);
    }

    @Test
    void sendEmail_NullNotification_ThrowsException() {
        // Arrange
        Notification nullNotification = null;

        // Act
        emailService.sendEmail(nullNotification);

        // Assert
        verifyNoInteractions(mailSender, modelMapper);
    }

    @Test
    void sendEmail_InvalidEmailFormat_ThrowsException() {
        // Arrange
        notification.setTo("invalid-email");

        // Act
        emailService.sendEmail(notification);

        // Assert
        verifyNoInteractions(mailSender, modelMapper);
    }

    @Test
    void sendEmail_ModelMapperThrowsException_EmailNotSent() {
        // Arrange
        when(modelMapper.map(eq(notification), eq(SimpleMailMessage.class)))
                .thenThrow(new RuntimeException("Mapping failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> emailService.sendEmail(notification));
        verifyNoInteractions(mailSender);
    }
}
