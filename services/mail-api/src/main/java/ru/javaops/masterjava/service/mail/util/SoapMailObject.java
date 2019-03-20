package ru.javaops.masterjava.service.mail.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.javaops.masterjava.service.mail.Attachment;

import java.util.List;

@Data
@NoArgsConstructor
public class SoapMailObject implements MailObject {
    @NonNull
    private String users;
    @NonNull private String subject;
    private String body;
    private List<Attachment> attachments;
}
