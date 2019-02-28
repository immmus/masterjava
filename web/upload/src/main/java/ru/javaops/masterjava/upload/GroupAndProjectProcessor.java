package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Map;

import static ru.javaops.masterjava.upload.UserProcessor.jaxbParser;

@Slf4j
public class GroupAndProjectProcessor {
    private GroupDao groupDao = DBIProvider.getDao(GroupDao.class);
    private ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException, JAXBException {
        val groups = groupDao.getAsMap();
        val projects = projectDao.getAsMap();
        val unmarshaller = jaxbParser.createUnmarshaller();

        while (processor.startElement("Project", "Projects")){
            val project = unmarshaller.unmarshal(processor.getReader(), Project.class);
            final String projectName = project.getName();
            if (!projects.containsKey(projectName)){
                final String description = project.getDescription();
                val newProject = new ru.javaops.masterjava.persist.model.Project(projectName, description);
                val insertProject = projectDao.insert(newProject);
                projects.put(projectName, insertProject);
            }
            for (Project.Group group : project.getGroup()) {
                final String groupName = group.getName();
                if(!groups.containsKey(groupName)){
                    final Integer projectId = projects.get(projectName).getId();
                    Group insertGroup =
                            groupDao.insert(new Group(groupName, GroupType.valueOf(group.getType().value()), projectId));
                    groups.put(groupName, insertGroup);
                }
            }
        }
        return groups;
    }
}
