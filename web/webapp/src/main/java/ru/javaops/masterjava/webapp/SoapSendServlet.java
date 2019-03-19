package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/sendSoap")
@Slf4j
@MultipartConfig
public class SoapSendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String result;
        try {
            log.info("Start sending");
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            String users = req.getParameter("users");
            String subject = req.getParameter("subject");
            String body = req.getParameter("body");
            Collection<Part> parts = req.getParts();
            GroupResult groupResult = MailWSClient.sendBulk(
                    MailWSClient.split(users),
                    subject, body,
                    getAttachments(parts)
            );
            result = groupResult.toString();
            log.info("Processing finished with result: {}", result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            result = e.toString();
        }
        resp.getWriter().write(result);
    }
    private static List<Attachment> getAttachments(Collection<Part> parts) throws IOException {
        List<Attachment> attachments =  new ArrayList<>();
        for (Part part : parts) {
            if (part.getName().startsWith("attachment")){
                try (InputStream is = part.getInputStream()){
                    attachments.add(Attachments.getAttachment(part.getSubmittedFileName(), is));
                }
            }
        }
        return attachments.size() > 0 ? attachments : ImmutableList.of();
    }
}
