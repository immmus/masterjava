package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;

import javax.mail.Authenticator;
import java.nio.charset.StandardCharsets;

public  class MailSettings {
    private static final MailSettings SETTINGS =
            new MailSettings(Configs.getConfig("mail.conf", "mail"));

    final private String host;
    final private int port;
    final private boolean useSSL;
    final private boolean useTLS;
    final private boolean debug;
    final private String username;
    final private Authenticator auth;
    final private String fromName;

    private MailSettings(Config conf) {
        this.host = conf.getString("host");
        this.port = Integer.parseInt(conf.getString("port"));
        this.useSSL = conf.getBoolean("useSSL");
        this.useTLS = conf.getBoolean("useSSL");
        this.debug = conf.getBoolean("debug");
        this.username = conf.getString("username");
        this.auth = new DefaultAuthenticator(username, conf.getString("password"));
        this.fromName = conf.getString("fromName");
    }

    public Email init(Email email) throws EmailException {
        email.setHostName(host);
        email.setSmtpPort(port);
        email.setAuthenticator(auth);
        email.setStartTLSEnabled(useTLS);
        email.setSSLOnConnect(useSSL);
        email.setDebug(debug);
        email.setFrom(username, fromName, StandardCharsets.UTF_8.name());
        return email;
    }
    public static Email createSimpleEmail() throws EmailException {
        return SETTINGS.init(new SimpleEmail());
    }
}
