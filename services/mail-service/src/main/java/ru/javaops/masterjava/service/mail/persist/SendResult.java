package ru.javaops.masterjava.service.mail.persist;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;
import ru.javaops.masterjava.persist.model.BaseEntity;
import ru.javaops.masterjava.service.mail.Addressee;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SendResult extends BaseEntity {
    private @Column("mail_to") String mailTo;
    private @Column("mail_cc") String mailCc;
    private String result;

    public static SendResult listsOf(Set<Addressee> mailsTo, Set<Addressee> mailsCc, String result) {
        return new SendResult(getMails(mailsTo), getMails(mailsCc), result);
    }

    private static String getMails(Set<Addressee> set) {
        return Optional.ofNullable(set).orElse(Collections.emptySet())
                .stream()
                .map(Addressee::getEmail)
                .collect(Collectors.joining(", "));
    }
}
