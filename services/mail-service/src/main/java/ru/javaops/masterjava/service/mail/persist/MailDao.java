package ru.javaops.masterjava.service.mail.persist;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import ru.javaops.masterjava.persist.dao.AbstractDao;

public abstract class MailDao implements AbstractDao<SendResult> {

    @SqlUpdate("INSERT INTO mail_Send_Result (mail_to, mail_cc, result)  VALUES (:mailTo, :mailCc, :result)")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean SendResult project);

    @Override
    @SqlUpdate("TRUNCATE mail_Send_Result")
    public abstract void clean();

    @Override
    public <T extends SendResult> T insert(T sendResult) {
        int id = insertGeneratedId(sendResult);
        sendResult.setId(id);
        return sendResult;
    }
}
