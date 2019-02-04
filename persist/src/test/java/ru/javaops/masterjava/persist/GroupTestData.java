package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.type.GroupType;

import java.util.List;

public class GroupTestData {
    public static Group TOPJAVA1;
    public static Group TOPJAVA2;
    public static Group TOPJAVA3;
    public static Group MASTERJAVA;
    public static List<Group> GROUPS;

    public static void init(){
        TOPJAVA1 = new Group("topjava06", GroupType.FINISHED);
        TOPJAVA2 = new Group("topjava07", GroupType.FINISHED);
        TOPJAVA3 = new Group("topjava08", GroupType.CURRENT);
        MASTERJAVA =  new Group("masterjava01", GroupType.CURRENT);
        GROUPS = ImmutableList.of(TOPJAVA1, TOPJAVA2, TOPJAVA3, MASTERJAVA);
    }

    public static void setUp() {
        GroupDao dao =  DBIProvider.getDao(GroupDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction(((conn, status) -> GROUPS.forEach(dao::insert)));
    }
}
