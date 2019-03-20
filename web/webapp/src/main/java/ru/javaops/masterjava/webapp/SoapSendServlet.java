package ru.javaops.masterjava.webapp;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.SoapMailObject;
import ru.javaops.masterjava.service.mail.util.WSutil;
import ru.javaops.masterjava.webapp.util.ServletUtil;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/sendSoap")
@Slf4j
@MultipartConfig
public class SoapSendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String result;
        try {
            log.info("Start sending");
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            SoapMailObject mailObject = ServletUtil.getMailObject(req, SoapMailObject.class);
            GroupResult groupResult = MailWSClient.sendBulk(
                    WSutil.split(mailObject.getUsers()),
                    mailObject.getSubject(),
                    mailObject.getBody(),
                    mailObject.getAttachments()
            );
            result = groupResult.toString();
            log.info("Processing finished with result: {}", result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            result = e.toString();
        }
        resp.getWriter().write(result);
    }

}
