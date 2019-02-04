package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao <Group> {
    @Override
    public <T extends Group> T insert(T group) {
        if (group.isNew()){
            int id = insertGenId(group);
            group.setId(id);
        } else {
            insertWithId(group);
        }
        return group;
    }
    @SqlUpdate("INSERT  into groups(id, name, type) values (:id, :name, CAST(:type AS group_type))")
    public abstract void insertWithId(@BindBean Group group);

    @SqlUpdate("INSERT into groups(name, type) values (:name, CAST(:type AS group_type))")
    @GetGeneratedKeys
    public abstract int insertGenId(@BindBean Group group);

    @SqlQuery("SELECT  * FROM groups")
    public abstract List<Group> getAll();

    @Override
    @SqlUpdate("TRUNCATE groups")
    public abstract void clean();
}
