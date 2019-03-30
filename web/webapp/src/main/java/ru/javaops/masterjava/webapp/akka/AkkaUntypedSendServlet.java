package ru.javaops.masterjava.webapp.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import ru.javaops.masterjava.util.Exceptions;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.javaops.masterjava.webapp.WebUtil.*;
import static ru.javaops.masterjava.webapp.akka.AkkaWebappListener.akkaActivator;

@WebServlet(
        value = "/sendAkkaUntyped",
        loadOnStartup = 1,
        asyncSupported = true,
        initParams = {
                @WebInitParam(name = "threadPoolSize", value = "8")
        })
@Slf4j
@MultipartConfig
public class AkkaUntypedSendServlet extends HttpServlet {
    private ActorRef mailActor;
    private ExecutorService executor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        executor = Executors.newFixedThreadPool(Integer.parseInt(getInitParameter("threadPoolSize")));
        mailActor = akkaActivator.getActorRef("akka.tcp://MailService@127.0.0.1:2553/user/mail-actor");
    }

    @Override
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        doAsync(resp, () -> {
            MailObject mailObject = createMailObject(req);
            final AsyncContext asyncContext = req.startAsync();
            executor.execute(
                    Exceptions.<Exception>wrap(() ->
                            sendAkka(mailObject, asyncContext))
            );
        });
    }

    private void sendAkka(MailObject mailObject, AsyncContext asyncContext) {
        ActorRef webappActor = akkaActivator.startActor(
                WebappActor.class,
                "untyped-mail-actor",
                asyncContext
        );
        mailActor.tell(mailObject, webappActor);
    }

    private static class WebappActor extends AbstractActor {
        private final AsyncContext asyncContext;

        public WebappActor(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().match(GroupResult.class,
                    groupResult -> {
                        log.info(groupResult.toString());
                        asyncContext.getResponse().getWriter().write(groupResult.toString());
                        asyncContext.complete();
                    })
                    .build();
        }
    }
}