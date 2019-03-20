package ru.javaops.masterjava.service.mail.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import ru.javaops.masterjava.service.mail.Attachment;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class JmsObject implements MailObject, Serializable {
    private static final long serialVersionUID = 42L;
    @NonNull
    private String users;
    @NonNull private String subject;
    private String body;
    private List<Pair<String, byte[]>> attachments;

}
