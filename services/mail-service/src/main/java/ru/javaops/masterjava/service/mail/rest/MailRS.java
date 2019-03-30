package ru.javaops.masterjava.service.mail.rest;


import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.media.multipart.*;
import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.util.MailUtils;
import ru.javaops.masterjava.web.WebStateException;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.AbstractMap.SimpleImmutableEntry;

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
                            final FormDataMultiPart multiPart) throws WebStateException {

        List<BodyPart> bodyParts = multiPart.getBodyParts();
        List<SimpleImmutableEntry<String, BodyPartEntity>> files = bodyParts.stream()
                .filter(part -> {
                    Map<String, String> parameters = part.getContentDisposition().getParameters();
                    return parameters.get("name").startsWith("attachment");
                })
                .map(part -> new SimpleImmutableEntry<>(
                        part.getContentDisposition().getFileName(),
                        (BodyPartEntity) part.getEntity())
                )
                .collect(Collectors.toList());

        final List<Attachment> attachments;
        if (files.size() > 0) {
            try {
                attachments = new ArrayList<>();
                for (SimpleImmutableEntry<String, BodyPartEntity> file : files) {
                    String attachName = file.getKey();
                    BodyPartEntity bodyPartEntity = file.getValue();
                    //          UTF-8 encoding workaround: https://java.net/jira/browse/JERSEY-3032
                    String utf8name = new String(attachName.getBytes("ISO8859_1"), StandardCharsets.UTF_8);
                    attachments.add(new Attachment(utf8name, new DataHandler((MailUtils.ProxyDataSource) bodyPartEntity::getInputStream)));
                }
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        } else {
            attachments = ImmutableList.of();
        }
        return MailServiceExecutor.sendBulk(MailUtils.split(users), subject, body, attachments);
    }
}