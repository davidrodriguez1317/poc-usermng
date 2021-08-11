package com.demo.usermng.identityprovider.messaging;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

public interface VhostConfiguration {

    String getVHost();

    ConnectionFactory connectionFactory();
}
