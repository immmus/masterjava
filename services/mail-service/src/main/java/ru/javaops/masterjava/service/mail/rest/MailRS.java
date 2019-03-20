package ru.javaops.masterjava.service.mail.rest;


import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.util.Attachments;
import ru.javaops.masterjava.service.mail.util.WSutil;
import ru.javaops.masterjava.web.WebStateException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/")
public class MailRS {

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test";
    }

    @POST
    @Path("send")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GroupResult send(@NotBlank @FormDataParam("users") String users,
                            @FormDataParam("subject") String subject,
                            @NotBlank @FormDataParam("body") String body,
                            final FormDataMultiPart multiPart) throws WebStateException, IOException {

        List<BodyPart> bodyParts = multiPart.getBodyParts();
        List<Pair<String, BodyPartEntity>> files = bodyParts.stream()
                .filter(part -> {
                    Map<String, String> parameters = part.getContentDisposition().getParameters();
                    return parameters.get("name").startsWith("attachment");
                })
                .map(part -> Pair.of(part.getContentDisposition().getFileName(),
                        (BodyPartEntity) part.getEntity()))
                .collect(Collectors.toList());

        List<Attachment> attachments;
        if (files.size() > 0) {
            attachments = new ArrayList<>();
            for (Pair<String, BodyPartEntity> file : files) {
                String fileName = file.getKey();
                try (InputStream is = file.getValue().getInputStream()) {
                    attachments.add(Attachments.getAttachment(fileName, is));
                }
            }
        } else {
            attachments = ImmutableList.of();
        }
        return MailServiceExecutor.sendBulk(WSutil.split(users), subject, body, attachments);
    }
}