package ru.javaops.masterjava.webapp;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.MailUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static ru.javaops.masterjava.webapp.WebUtil.*;

@WebServlet("/sendSoap")
@Slf4j
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, //10 MB in memory limit
        maxFileSize = 1024 * 1024 * 25)
public class SoapSendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        doAndWriteResponse(resp, () -> {
            String users = getNotEmptyParam(req, "users");
            String subject = req.getParameter("subject");
            String body = getNotEmptyParam(req, "body");

            List<Attachment> attachments = createAttachments(req);
            GroupResult groupResult = MailWSClient.sendBulk(MailUtils.split(users), subject, body, attachments);
            return groupResult.toString();
        });
    }
}
