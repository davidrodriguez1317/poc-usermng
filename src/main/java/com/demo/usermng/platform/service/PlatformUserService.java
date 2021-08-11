package com.demo.usermng.platform.service;

import com.demo.usermng.converter.EntityMapper;
import com.demo.usermng.platform.dto.PlatformUserDto;
import com.demo.usermng.repository.UserRepository;
import com.demo.usermng.repository.entity.UserEntity;
import com.demo.usermng.repository.keycloakserver.KeycloakService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class PlatformUserService {

    private UserRepository userRepository;
    private KeycloakService keycloakService;

    private final EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    public Collection<PlatformUserDto> getUsersByRealmId(String realmId) {
        Collection<UserEntity> userEntities = userRepository.getUsersByRealm(realmId);

        return userEntities.stream()
                .map(mapper::fromUserEntityToPlatformDto)
                .collect(Collectors.toUnmodifiableList());
    }


    public Map<String, String> getUserNamesMappedByUserIdForRealm(String realmId) {
        Collection<UserEntity> userEntities = userRepository.getUsersByRealm(realmId);

        Map<String, UserEntity> userEntityWithoutUserIdList = userEntities.stream()
                .filter(u -> u.getUserId() == null)
                .collect(Collectors.toMap(UserEntity::getUserName, Function.identity()));

        // TODO This way of getting the userId has to be refactored
        if (!userEntityWithoutUserIdList.isEmpty()) {
            fillUserEntitiesWithEmptyUserId(realmId, userEntityWithoutUserIdList);
            userEntities = userRepository.getUsersByRealm(realmId);
        }

        log.info("getUserNamesMappedByUserIdForRealm- Users entities after filling userId= " + userEntities);


        Map<String, String> mappingToUserNames = userEntities.stream()
                .filter(u -> Objects.nonNull(u.getUserId()))
                .collect(Collectors.toMap(UserEntity::getUserId, UserEntity::getUserName));

        log.info("getUserNamesMappedByUserIdForRealm- mappingToUserName= " + mappingToUserNames);

        return mappingToUserNames;
    }

    private void fillUserEntitiesWithEmptyUserId(String realmId, Map<String, UserEntity> userEntityWithoutUserIdList) {
        List<UserRepresentation> userRepresentationList = keycloakService.getAllUsersOfRealmFromKeycloak(realmId);

        log.info("Users without userId= " + userEntityWithoutUserIdList);

        log.info("Users in keycloak size= " + userRepresentationList.size());

        userRepresentationList.stream()
                .forEach(
                        u -> log.info("User in keycloak= " + u.getUsername())
                );

        for (UserRepresentation userRepresentation : userRepresentationList) {
            if (userEntityWithoutUserIdList.containsKey(userRepresentation.getUsername())) {
                UserEntity userEntity = userEntityWithoutUserIdList.get(userRepresentation.getUsername());
                userEntity.setUserId(userRepresentation.getId());
                userRepository.setUserIdToUserEntity(userEntity);
                log.info(String.format("UserId= %s set for UserName= %s in realm= %s",
                        userEntity.getUserId(),
                        userEntity.getUserName(),
                        realmId));
            }
        }
    }
}
