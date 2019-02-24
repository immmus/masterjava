package ru.javaops.masterjava.service.mail;

import javax.jws.WebService;
import java.util.Set;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService")
public class MailServiceImpl implements MailService {
    public void sendMail(Set<Addressee> to, Set<Addressee> cc, String subject, String body) {
        MailSender.sendMail(to, cc, subject, body);
    }
}