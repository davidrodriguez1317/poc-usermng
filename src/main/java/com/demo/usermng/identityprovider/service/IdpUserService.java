package com.demo.usermng.identityprovider.service;

import com.demo.usermng.converter.EntityMapper;
import com.demo.usermng.identityprovider.dto.IdpUserEventDto;
import com.demo.usermng.identityprovider.messaging.MessageService;
import com.demo.usermng.repository.UserRepository;
import com.demo.usermng.repository.entity.UserEntity;
import com.demo.usermng.repository.keycloakserver.KeycloakService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Service
public class IdpUserService {

    private final UserRepository userRepository;
    private final MessageService messageService;
    private final KeycloakService keycloakService;

    private final EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    @Autowired
    public IdpUserService(
            @Qualifier("userRepository") UserRepository userRepository,
            @Qualifier("messageService") MessageService messageService,
            @Qualifier("keycloakService") KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.keycloakService = keycloakService;
    }

    public void handleUserEvent(IdpUserEventDto userEventDto) {

        switch (userEventDto.getOperation().toLowerCase()) {
            case "create":
                createUser(userEventDto);
                break;
            case "update":
                updateUser(userEventDto);
                break;
            case "remove":
                removeUser(userEventDto);
                break;
            default:
                throw new RuntimeException("Unsupported operation: " + userEventDto.getOperation());
        }

        messageService.sendUserMessage(
                mapper.fromIdpUserEventDtoToMessage(userEventDto),
                userEventDto.getRealmId());

    }

    private void createUser(IdpUserEventDto userEventDto) {
        UserEntity userEntity = mapper.fromIdpUserEventDtoToEntity(userEventDto);
        userEntity.setCreationTime(Date.from(Instant.now()));
        userRepository.createUser(userEntity);
    }

    private void updateUser(IdpUserEventDto userEventDto) {
        UserEntity oldUserEntity = userRepository.getUserByUserName(userEventDto.getRealmId(), userEventDto.getUserName());
        if (Objects.isNull(oldUserEntity)) {
            createUser(userEventDto);
        } else {
            Date creationTime = oldUserEntity.getCreationTime();
            UserEntity userEntity = mapper.fromIdpUserEventDtoToEntity(userEventDto);
            userEntity.setCreationTime(creationTime);
            userEntity.setModificationTime(Date.from(Instant.now()));
            userRepository.updateUser(userEntity);
        }
    }

    private void removeUser(IdpUserEventDto userEventDto) {
        userRepository.removeUser(userEventDto.getRealmId(), userEventDto.getUserName());
    }
}
