package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

public class CityTestData {
    public static City SPB;
    public static City KIEV;
    public static City MOSCOW;
    public static City MINSK;
    public static List<City> CITIES;

    public static void init() {
        SPB = new City("spb", "Санкт-Петербург");
        KIEV = new City("mow", "Москва");
        MOSCOW =  new City("kiv", "Киев");
        MINSK = new City("mnsk", "Минск");
        CITIES = ImmutableList.of(SPB, KIEV, MOSCOW, MINSK);
    }
    public static void setUp() {
        CityDao dao =  DBIProvider.getDao(CityDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) ->
                CITIES.forEach(dao::insert)
        );
    }
}
