package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.mail.persist.MailDao;
import ru.javaops.masterjava.service.mail.persist.SendResult;

import java.util.Set;

@Slf4j
public class MailSender {
    private static final MailDao dao = DBIProvider.getDao(MailDao.class);
    static void sendMail(Set<Addressee> to, Set<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        String result = "complete";
        try {
            Email email = MailSettings.createSimpleEmail();
            email.setSubject(subject);
            email.setMsg(body);
            for (Addressee addr : to) {
                email.addTo(addr.getEmail(), addr.getName());
            }
            for (Addressee addr : cc) {
                email.addCc(addr.getEmail(), addr.getName());
            }
            email.send();
        } catch (EmailException e) {
             e.printStackTrace();
             result = "Error: "  + e.getMessage();
        }
        dao.insert(SendResult.setsOf(to, cc, result));
    }
}
