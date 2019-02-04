package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.GroupTestData;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

public class GroupDaoTest extends AbstractDaoTest<GroupDao> {

    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @BeforeClass
    public static void init(){
        GroupTestData.init();
    }
    @Before
    public void setUp() {
        GroupTestData.setUp();
    }
    @Test
    public void getAll(){

        List<Group> groups = dao.getAll();
        Assert.assertEquals(GroupTestData.GROUPS, groups);
    }
}
