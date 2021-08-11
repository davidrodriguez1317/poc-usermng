package com.demo.usermng.repository;

import com.demo.usermng.exception.RealmHandlingException;
import com.demo.usermng.repository.entity.LoginEventEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Log4j2
@Repository
public class LoginEventRepository {

    // TODO Change to hibernate
    // TODO map to container class
    private static Map<String, List<LoginEventEntity>> loginEventsByRealm = new HashMap<>();

    public void addLoginEvent(LoginEventEntity loginEvent) {
        String realmId = loginEvent.getRealmId();
        if (!loginEventsByRealm.containsKey(realmId)) {
            loginEventsByRealm.put(realmId, new ArrayList<>());
        }
        loginEvent.setLogTime(Date.from(Instant.now()));
        loginEventsByRealm.get(realmId).add(loginEvent);
    }


    public Collection<LoginEventEntity> getLoginEventsByRealmId(String realmId) throws RealmHandlingException {
        if (!loginEventsByRealm.containsKey(realmId)) {
            throw new RealmHandlingException("Realm does not exist - " + realmId);
        }
        return loginEventsByRealm.get(realmId);
    }
}
