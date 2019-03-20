package ru.javaops.masterjava.service.mail.listeners;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.tuple.Pair;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.util.Attachments;
import ru.javaops.masterjava.service.mail.util.JmsObject;
import ru.javaops.masterjava.service.mail.util.WSutil;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.List;

@WebListener
@Slf4j
public class JmsMailListener implements ServletContextListener {
    private Thread listenerThread = null;
    private QueueConnection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            InitialContext initCtx = new InitialContext();
            ActiveMQConnectionFactory connectionFactory =
                    (ActiveMQConnectionFactory) initCtx.lookup("java:comp/env/jms/ConnectionFactory");
            connectionFactory.setTrustAllPackages(true);

            connection = connectionFactory.createQueueConnection();
            QueueSession queueSession = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) initCtx.lookup("java:comp/env/jms/queue/MailQueue");
            QueueReceiver receiver = queueSession.createReceiver(queue);
            connection.start();
            log.info("Listen JMS messages ...");
            listenerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        Message m = receiver.receive();
                        // TODO implement mail sending
                        if (m instanceof ObjectMessage) {
                            ObjectMessage objectMessage = (ObjectMessage) m;
                            JmsObject object = (JmsObject) objectMessage.getObject();
                            String users = object.getUsers();
                            String subject = object.getSubject();
                            String body = object.getBody();
                            List<Pair<String, byte[]>> pairs = object.getAttachments();
                            List<Attachment> attachments;
                            if (!pairs.isEmpty()) {
                                attachments = new ArrayList<>();
                                for (Pair<String, byte[]> pair : pairs) {
                                    Attachment attachment = Attachments.getAttachment(pair.getKey(), pair.getValue());
                                    attachments.add(attachment);
                                }
                            } else attachments = ImmutableList.of();
                            MailServiceExecutor.sendBulk(WSutil.split(users), subject, body, attachments);
                            log.info("Received ObjectMessage with text '{}'", objectMessage.toString());
                        }
                    }
                } catch (Exception e) {
                    log.error("Receiving messages failed: " + e.getMessage(), e);
                }
            });
            listenerThread.start();
        } catch (Exception e) {
            log.error("JMS failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ex) {
                log.warn("Couldn't close JMSConnection: ", ex);
            }
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }
}