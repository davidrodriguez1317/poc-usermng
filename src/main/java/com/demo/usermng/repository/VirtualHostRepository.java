package com.demo.usermng.repository;

import com.demo.usermng.repository.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class VirtualHostRepository {

    // TODO Change to hibernate
    // TODO map to container class
    private static Map<String, String> realmToVHostMapping;

    static {
        realmToVHostMapping = new HashMap<>();
        realmToVHostMapping.put("platform-tenant01-realm", "platform_tenant01");
        realmToVHostMapping.put("platform-tenant02-realm", "platform_tenant02");
    }

    public String getVHostFromRealmId(String realmId) {
        return realmToVHostMapping.get(realmId);
    }
}
