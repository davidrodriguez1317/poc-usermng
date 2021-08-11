package com.demo.usermng.repository;

import com.demo.usermng.exception.RealmHandlingException;
import com.demo.usermng.exception.UserHandlingException;
import com.demo.usermng.repository.entity.UserEntity;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class UserRepository {

    // TODO Change to hibernate
    // TODO map to container class
    private static Map<String, Map<String, UserEntity>> usersByRealm = new HashMap<>();

    public void createUser(UserEntity user) {
        String realmId = user.getRealmId();
        log.info("realmId " + realmId);

        if (usersByRealm.containsKey(realmId)) {
            log.info("realm users size " + usersByRealm.get(realmId).size());
            Map<String, UserEntity> users = usersByRealm.get(realmId);
            log.info("Users before (inserting)= " + users);

            users.put(user.getUserName(), user);
            usersByRealm.put(realmId, users);
            log.info("Current users (inserting)= " + users);
        } else {
            Map<String, UserEntity> users = new HashMap<>();
            users.put(user.getUserName(), user);
            usersByRealm.put(realmId, users);
            log.info("Current users (inserting)= " + users);

        }
    }

    public void updateUser(UserEntity user) {
        Map<String, UserEntity> users = usersByRealm.get(user.getRealmId());
        users.put(user.getUserName(), user);
        usersByRealm.put(user.getRealmId(), users);
        log.info("Current users (updating)= " + users);
    }

    public void removeUser(String realmId, String userName) throws UserHandlingException {
        getUser(realmId, userName);
        usersByRealm.get(realmId).remove(userName);
    }

    public UserEntity getUserByUserName(String realmId, String userName) throws UserHandlingException {
        return getUser(realmId, userName);
    }

    private UserEntity getUser(String realmId, String userName) throws UserHandlingException {
        log.info(String.format("User repository get User. UserName=%s, RealmId= %s", userName, realmId));
        Map<String, UserEntity> users = usersByRealm.entrySet().stream()
                .filter(s -> s.getKey().equalsIgnoreCase(realmId))
                .map(Map.Entry::getValue)
                .findFirst().orElseThrow(() -> new RealmHandlingException("Realm does not exist - " + realmId));

        UserEntity user = users.get(userName);
        if (user == null) {
            throw new UserHandlingException(String.format("User %s does not exist in realm %s",
                    userName, realmId));
        }
        return user;
    }

    public Collection<UserEntity> getUsersByRealm(String realmId) throws RealmHandlingException {
        Map<String, UserEntity> users = usersByRealm.entrySet().stream()
                .filter(s -> s.getKey().equalsIgnoreCase(realmId))
                .map(Map.Entry::getValue)
                .findFirst().orElseThrow(() -> new RealmHandlingException("Realm not found - " + realmId));

        log.info("Current users (reading)= " + users);
        return users.values();
    }

    public void setUserIdToUserEntity(UserEntity userEntity) {
        Map<String, UserEntity> users = usersByRealm.get(userEntity.getRealmId());
        log.info("Addind userId to user = " + userEntity.toString());

        users.put(userEntity.getUserName(), userEntity);
        usersByRealm.put(userEntity.getRealmId(), users);
        log.info("Current users (inserting)= " + users);
    }
}
