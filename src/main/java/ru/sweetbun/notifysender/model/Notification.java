package ru.sweetbun.notifysender.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private String to;
    private String subject;
    private String text;
}
