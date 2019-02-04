package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {
    @Override
    @SqlUpdate("TRUNCATE cities CASCADE ")
    public abstract void clean();

    @SqlQuery("SELECT * from cities")
    public abstract List<City> getAll();

    @SqlUpdate("INSERT  INTO  cities(id, name) values (:id, :name)")
     public abstract void insert(@BindBean City city);

    @SqlBatch("INSERT  INTO cities(id, name) values (:id, :name) ON CONFLICT DO NOTHING ")
    public abstract int[] insertBatch(@BindBean List<City> cities);
}
