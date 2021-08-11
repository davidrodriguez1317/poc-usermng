package com.demo.usermng.identityprovider.messaging;

import com.demo.usermng.repository.VirtualHostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class VirtualHostFactory {

    @Value("${spring.rabbitmq.host}")
    private final String host;

    @Value("${spring.rabbitmq.port}")
    //@Value("#{new Integer('${spring.rabbitmq.port}')}")
    private final String port;

    @Value("${spring.rabbitmq.username}")
    private final String username;

    @Value("${spring.rabbitmq.password}")
    private final String password;

    private final VirtualHostRepository virtualHostRepository;

    private static Map<String, ConnectionFactory> connectionFactoriesByRealmId = new HashMap<>();

    public ConnectionFactory getConnectionFactoryByRealmId(String realmId) {

        log.info("VHostFactory. Realm required= %s, CurrentRealms= %s",
                realmId, connectionFactoriesByRealmId.keySet().toString());

        if (!connectionFactoriesByRealmId.containsKey(realmId)) {
            String vHostId = virtualHostRepository.getVHostFromRealmId(realmId);
            ConnectionFactory connFactory = buildConnectionFactoryForVHostId(vHostId);
            connectionFactoriesByRealmId.put(realmId, connFactory);
        }

        return connectionFactoriesByRealmId.get(realmId);
    }


    // TODO It builds using the general user (test/test). If the user has to be per vHost
    // TODO then the users/passwords are to be stored
    private ConnectionFactory buildConnectionFactoryForVHostId(String vHostId) {
        CachingConnectionFactory connFactory = new CachingConnectionFactory(host);
        connFactory.setVirtualHost(vHostId);
        connFactory.setPort(Integer.parseInt(port));
        connFactory.setUsername(username);
        // TODO add encryption/decryption
        connFactory.setPassword(password);
        return connFactory;
    }
}
