package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.CityTestData;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

public class CityDaoTest extends AbstractDaoTest<CityDao>{
    public CityDaoTest() {
        super(CityDao.class);
    }
    @BeforeClass
    public static void init(){
        CityTestData.init();
    }
    @Before
    public void setUp() {
        CityTestData.setUp();
    }
    @Test
    public void getAllTest () {
        List<City> cities = dao.getAll();
        Assert.assertEquals(CityTestData.CITIES, cities);
    }
    @Test
    public void insertTest() {
        City city = new City("kzn", "Kazan");
        dao.insert(city);
    }
}
