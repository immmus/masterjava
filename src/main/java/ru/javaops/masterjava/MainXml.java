package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainXml {
    public static void main(String[] args) throws Exception {
        JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
        jaxbParser.setSchema(Schemas.ofClasspath("payload.xsd"));
        InputStream is = Resources.getResource("payload.xml").openStream();
        Payload unmarshal = jaxbParser.unmarshal(is);

        List<User> users = jaxbRealization(args[0], unmarshal);
        users.forEach(user -> System.out.println(user.getFullName() + " - " + user.getEmail()));
        System.out.println(htmlTable(users));
    }

    private static List<User> jaxbRealization(String projectName, Payload unmarshal) {
        return unmarshal.getUsers().getUsersList().stream()
                .filter(user -> user.getInGroups().contains(projectName))
                .sorted(Comparator.comparing(User::getFullName).thenComparing(User::getEmail))
                .collect(Collectors.toList());
    }
    private static String htmlTable(List<User> users) throws IOException {
        if (users == null){
            throw new RuntimeException();
        }
        StringBuilder buf = new StringBuilder();
        buf.append("<html>" +
                "<body>" +
                "<table border=\"1\" cellpadding=\"8\" cellspacing=\"0\">" +
                "<tr>" +
                "<th>Full Name</th>" +
                "<th>Email</th>" +
                "</tr>");
        for (User user : users) {
            buf.append("<tr><td>")
                    .append(user.getFullName())
                    .append("</td><td>")
                    .append(user.getEmail())
                    .append("</td></tr>");
        }
        buf.append("</table>" +
                "</body>" +
                "</html>");
        File file =  File.createTempFile("table", ".html", new File("src/main/resources"));
        Files.newOutputStream(file.toPath().toAbsolutePath()).write(buf.toString().getBytes());
        return buf.toString();
    }
}
