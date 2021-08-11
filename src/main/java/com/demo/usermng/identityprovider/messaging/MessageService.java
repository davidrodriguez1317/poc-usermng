package com.demo.usermng.identityprovider.messaging;

import com.demo.usermng.identityprovider.messaging.message.PlatformUserMessage;
import com.demo.usermng.repository.VirtualHostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static com.demo.usermng.utils.Constants.*;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Log4j2
@Service
@AllArgsConstructor
public class MessageService {

    private final TopicExchange exchange;

    private final RabbitTemplate rabbitTemplate;

    private final String routingKeyPrefix;

    private final VirtualHostFactory virtualHostFactory;


    public void sendUserMessage(PlatformUserMessage platformUserMessage, String realmId) {

        String routingKey = routingKeyPrefix
                .concat(MESSAGE_PREFIX_USER)
                .concat(platformUserMessage.getOperation().toLowerCase());

        try {

            ConnectionFactory connectionFactory = virtualHostFactory.getConnectionFactoryByRealmId(realmId);

            log.info(String.format("Publishing message to vHost= %s with routingKey= %s",
                    connectionFactory.getVirtualHost(), routingKey));

            rabbitTemplate.setConnectionFactory(connectionFactory);
            rabbitTemplate.convertAndSend(
                    exchange.getName(), routingKey, platformUserMessage);


        } catch (AmqpException e) {
            log.error(
                    format("There was an error sending a message from RealmId= %s, routingKey= %s, exchange= %s with message= %s",
                            realmId, routingKey, exchange.getName(), platformUserMessage.toString()));
        }
    }
}
