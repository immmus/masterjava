package ru.javaops.masterjava;


import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@WebServlet(value = "/")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10)
public class FileUploadServlet extends HttpServlet {
    private static final JaxbParser parser = new JaxbParser(ObjectFactory.class);
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/views/fileUpload.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart("fileXml");
        Set<User> users = new TreeSet<>(USER_COMPARATOR);
        try (StaxStreamProcessor processor = new StaxStreamProcessor(filePart.getInputStream())) {
            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                User user = parser.unmarshal(processor.getReader(), User.class);
                users.add(user);
            }
        } catch (XMLStreamException | JAXBException e) {/**/}
        if (users.size() > 0) {
            req.setAttribute("users", users);
            req.getRequestDispatcher("WEB-INF/views/result.jsp").forward(req, resp);
        } else resp.sendRedirect("/");
    }
}
