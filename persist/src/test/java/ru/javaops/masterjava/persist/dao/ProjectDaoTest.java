package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.ProjectTestData;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {

    public ProjectDaoTest() {

        super(ProjectDao.class);
    }
    @BeforeClass
    public static void init(){
        ProjectTestData.init();
    }
    @Before
    public void setUp(){
        ProjectTestData.setUp();
    }
    @Test
    public void getAll() {
        List<Project> projects = dao.getAll();
        Assert.assertEquals(ProjectTestData.PROJECTS, projects);
    }
}
