package ru.javaops.masterjava.upload;

import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CityProcessor {
    private static CityDao userDao = DBIProvider.getDao(CityDao.class);

    public Map<String, City> process (final StaxStreamProcessor processor) throws XMLStreamException {
        Map<String, City> cityFromDb = StreamEx.of(userDao.getAll()).toMap(City::getId, Function.identity());
        List<City> uploadedCities = new ArrayList<>();
        while (processor.startElement( "City", "Cities")){
            final String id = processor.getAttribute("id");
            if (!cityFromDb.containsKey(id)) {
                City city = new City(id, processor.getText());
                uploadedCities.add(city);
                cityFromDb.put(id, city);
            }
        }
        userDao.insertBatch(uploadedCities);
        return cityFromDb;
    }
}
