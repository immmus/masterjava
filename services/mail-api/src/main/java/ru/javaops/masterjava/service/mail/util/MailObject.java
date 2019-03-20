package ru.javaops.masterjava.service.mail.util;

public interface MailObject {
    void setUsers(String users);
    void setSubject(String subject);
    void setBody(String body);
}
