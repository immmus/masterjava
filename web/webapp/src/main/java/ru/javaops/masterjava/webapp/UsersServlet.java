package ru.javaops.masterjava.webapp;

import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("")
public class UsersServlet extends HttpServlet {
    private final UserDao dao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        List<User> users = dao.getAll(20);
        webContext.setVariable("usersFromBase", users);
        engine.process("users", webContext, resp.getWriter());
    }
}
