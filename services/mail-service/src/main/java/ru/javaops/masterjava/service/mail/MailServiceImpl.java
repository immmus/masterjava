package ru.javaops.masterjava.service.mail;

import com.sun.xml.ws.developer.StreamingAttachment;
import ru.javaops.web.WebStateException;

import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.util.List;
import java.util.Set;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService", targetNamespace = "http://mail.javaops.ru/"
//          , wsdlLocation = "WEB-INF/wsdl/mailService.wsdl"
)
@StreamingAttachment(parseEagerly=true, memoryThreshold=40000L)
@MTOM
public class MailServiceImpl implements MailService {
    @Override
    public String sendToGroup(Set<Addressee> to, Set<Addressee> cc, String subject, String body, List<Attachment> files) throws WebStateException {
        return MailSender.sendToGroup(to, cc, subject, body, files);
    }

    @Override
    public GroupResult sendBulk(Set<Addressee> to, String subject, String body, List<Attachment> files) throws WebStateException {
        return MailServiceExecutor.sendBulk(to, subject, body, files);
    }
}