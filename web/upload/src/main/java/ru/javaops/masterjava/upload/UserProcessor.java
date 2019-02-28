package ru.javaops.masterjava.upload;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserGroup;
import ru.javaops.masterjava.persist.model.type.UserFlag;
import ru.javaops.masterjava.upload.PayloadProcessor.FailedEmails;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Strings.nullToEmpty;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);
    private static UserGroupDao userGroupDao = DBIProvider.getDao(UserGroupDao.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    /*
     * return failed users chunks
     */
    public List<FailedEmails> process(final StaxStreamProcessor processor, Map<String, City> cities, Map<String, Group> groups, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)

        int id = userDao.getSeqAndSkip(chunkSize);
        List<User> chunk = new ArrayList<>(chunkSize);
        List<UserGroup> allImportedUsersGroups = new ArrayList<>();
        val unmarshaller = jaxbParser.createUnmarshaller();
        List<FailedEmails> failed = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            String cityRef = processor.getAttribute("city");
            String groupRefs = processor.getAttribute("groupRefs");// unmarshal doesn't get city ref
            List<String> namesGroup = groupRefs == null
                    ? Collections.emptyList()
                    : Splitter.on(' ').splitToList(nullToEmpty(groupRefs));

            val xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            if(!groups.keySet().containsAll(namesGroup)){
                failed.add(new FailedEmails(xmlUser.getEmail(), "Group '" + groupRefs + "' is not present in DB"));
            } else {
                if (cities.get(cityRef) == null) {
                    failed.add(new FailedEmails(xmlUser.getEmail(), "City '" + cityRef + "' is not present in DB"));
                } else {
                    final User user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()), cityRef);
                    final List<UserGroup> userGroups = StreamEx.of(namesGroup)
                            .filter(name -> groups.keySet().contains(name))
                            .map(groups::get)
                            .map(Group::getId)
                            .map(groupId -> new UserGroup(user.getId(), groupId))
                            .toList();
                    allImportedUsersGroups.addAll(userGroups);
                    chunk.add(user);

                    if (chunk.size() == chunkSize) {
                        addChunkFutures(chunkFutures, chunk, allImportedUsersGroups);
                        chunk = new ArrayList<>(chunkSize);
                        allImportedUsersGroups = new ArrayList<>();
                        id = userDao.getSeqAndSkip(chunkSize);
                    }
                }
            }
        }

        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk, allImportedUsersGroups);
        }

        List<String> allAlreadyPresents = new ArrayList<>();
        chunkFutures.forEach((emailRange, future) -> {
            try {
                List<String> alreadyPresentsInChunk = future.get();
                log.info("{} successfully executed with already presents: {}", emailRange, alreadyPresentsInChunk);
                allAlreadyPresents.addAll(alreadyPresentsInChunk);
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));
            }
        });
        if (!allAlreadyPresents.isEmpty()) {
            failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
        }
        return failed;
    }

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> chunk, List<UserGroup> userGroups) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<List<String>> future = executorService.submit(() -> {
            Map<Integer, User> conflictEmailUsers = userDao.insertAndGetConflictEmailUsers(chunk);
            List<UserGroup> toInsertUG = StreamEx.of(userGroups)
                    .filter(ug -> !conflictEmailUsers.containsKey(ug.getUserId()))
                    .toList();
            userGroupDao.insertBatch(toInsertUG);
            return StreamEx.of(conflictEmailUsers.values()).map(User::getEmail).toList();
        });
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }
}
