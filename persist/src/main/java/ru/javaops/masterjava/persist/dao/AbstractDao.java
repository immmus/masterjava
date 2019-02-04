package ru.javaops.masterjava.persist.dao;

import ru.javaops.masterjava.persist.model.BaseEntity;

public interface AbstractDao<Entity extends BaseEntity> {
    void clean();
     <T extends Entity> T  insert (T bean);
}
