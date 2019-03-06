package ru.javaops.masterjava.webapp;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import one.util.streamex.StreamEx;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.util.StringUtils;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.MailWSClient;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("")
public class UsersServlet extends HttpServlet {
    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  throws IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("users", userDao.getWithLimit(20)));
        engine.process("users", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String emails = req.getParameter("emails");
        String body = req.getParameter("body");
        String subject = req.getParameter("subject");
        if (!StringUtils.isEmpty(emails)) {
            Set<Addressee> to = StreamEx.of(Splitter.on(',').splitToList(emails)).map(Addressee::new).toSet();
            MailWSClient.sendBulk(to, subject, body);
        }
    }
}
