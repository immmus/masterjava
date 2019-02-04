package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao<Project>{

    @Override
    public <T extends Project> T insert(T project) {
        if (project.isNew()) {
            int id = insertGenId(project);
            project.setId(id);
        } else {
            insertWithId(project);
        }
        return project;
    }

    @Override
    @SqlUpdate("TRUNCATE projects")
    public abstract void clean();

    @SqlQuery("SELECT * from projects")
    public abstract List<Project> getAll();

    @SqlUpdate("INSERT  INTO  projects(name, description) values (:name, :description)")
    @GetGeneratedKeys
    public abstract int insertGenId(@BindBean Project project);

    @SqlUpdate("INSERT  INTO  projects(id, name, description) values (:id, :name, :description)")
    public abstract int insertWithId(@BindBean Project project);
}
