package ru.javaops.masterjava.webapp;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.util.MailUtils;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import ru.javaops.masterjava.util.Functions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class WebUtil {
    private final static String FILE_ATTACH = "attachment";

    public static void doAsync(HttpServletResponse resp, Functions.RunnableEx doer) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        try {
            log.info("Start asynchronous processing");
            doer.run();
            log.info("Asynchronous processing running ...");
        } catch (Exception e) {
            log.error("Asynchronous processing failed", e);
            String message = e.getMessage();
            String result = (message != null) ? message : e.getClass().getName();
            resp.getWriter().write(result);
        }
    }

    public static void doAndWriteResponse(HttpServletResponse resp, Functions.SupplierEx<String> doer) throws IOException {
        log.info("Start sending");
        resp.setCharacterEncoding("UTF-8");
        String result;
        try {
            log.info("Start processing");
            result = doer.get();
            log.info("Processing finished with result: {}", result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            String message = e.getMessage();
            result = (message != null) ? message : e.getClass().getName();
        }
        resp.getWriter().write(result);
    }

    public static String getNotEmptyParam(HttpServletRequest req, String param) {
        String value = req.getParameter(param);
        checkArgument(!Strings.isNullOrEmpty(value), param + " must not be empty");
        return value;
    }

    public static MailObject createMailObject(HttpServletRequest req) throws IOException, ServletException {
        List<SimpleImmutableEntry<String, byte[]>> attachments = null;
        for (Part part : req.getParts()) {
            if (part.getName().startsWith(FILE_ATTACH)) {
                if (attachments == null) {
                    attachments = new ArrayList<>();
                }
                attachments.add(
                        new SimpleImmutableEntry<>(
                                part.getSubmittedFileName(),
                                IOUtils.toByteArray(part.getInputStream())
                        ));
            }
        }
        return new MailObject(getNotEmptyParam(req, "users"), req.getParameter("subject"), getNotEmptyParam(req, "body"),
                attachments == null ? ImmutableList.of() : ImmutableList.copyOf(attachments));
    }

    public static List<Attachment> createAttachments(HttpServletRequest req) throws IOException, ServletException {
        List<Attachment> attachments = null;
        for (Part part : req.getParts()) {
            if (part.getName().startsWith(FILE_ATTACH)) {
                if (attachments == null) {
                    attachments = new ArrayList<>();
                }
                attachments.add(
                        MailUtils.getAttachment(
                                part.getSubmittedFileName(),
                                part.getInputStream())
                );
            }
        }
        return attachments == null ? ImmutableList.of() : ImmutableList.copyOf(attachments);
    }
}
