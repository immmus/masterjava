package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.web.AuthUtil;
import ru.javaops.masterjava.web.handler.SoapServerSecurityHandler;

public class MailSecurityHandler extends SoapServerSecurityHandler {
    public MailSecurityHandler() {
        super(AuthUtil.encodeBasicAuthHeader(
                MailWSClient.getWsHostConfig().getUser(),
                MailWSClient.getWsHostConfig().getPassword()));
    }
}
