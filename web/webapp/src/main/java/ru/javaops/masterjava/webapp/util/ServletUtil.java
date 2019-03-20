package ru.javaops.masterjava.webapp.util;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.util.Attachments;
import ru.javaops.masterjava.service.mail.util.JmsObject;
import ru.javaops.masterjava.service.mail.util.MailObject;
import ru.javaops.masterjava.service.mail.util.SoapMailObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServletUtil {
    public static <T extends MailObject> T getMailObject(HttpServletRequest req, Class<T> tClass) throws IOException, ServletException, IllegalAccessException, InstantiationException {
        T object = tClass.newInstance();
        object.setUsers(req.getParameter("users"));
        object.setSubject(req.getParameter("subject"));
        object.setBody(req.getParameter("body"));
        if (object instanceof JmsObject) {
            ((JmsObject) object).setAttachments(getJmsAttachments(req.getParts()));
        } else if (object instanceof  SoapMailObject) {
            ((SoapMailObject) object).setAttachments(getSoapAttachments(req.getParts()));
        }
        return object;
    }

    public static List<Attachment> getSoapAttachments(Collection<Part> parts) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        for (Part part : parts) {
            if (part.getName().startsWith("attachment")) {
                try (InputStream is = part.getInputStream()) {
                    attachments.add(Attachments.getAttachment(part.getSubmittedFileName(), is));
                }
            }
        }
        return attachments.size() > 0 ? attachments : ImmutableList.of();
    }
    public static List<Pair<String, byte[]>> getJmsAttachments (Collection<Part> parts) throws IOException {
        List<Pair<String, byte[]>>attachments = new ArrayList<>();
        for (Part part : parts) {
            if (part.getName().startsWith("attachment")) {
                try (InputStream is = part.getInputStream()) {
                    Pair<String, byte[]> pair = Pair.of(part.getSubmittedFileName(), IOUtils.toByteArray(is));
                    attachments.add(pair);
                }
            }
        }
        return attachments.size() > 0 ? attachments : ImmutableList.of();
    }
}
