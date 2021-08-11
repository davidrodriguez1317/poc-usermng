package com.demo.usermng.platform.service;

import com.demo.usermng.converter.EntityMapper;
import com.demo.usermng.platform.dto.PlatformLoginEventDto;
import com.demo.usermng.platform.dto.PlatformUserDto;
import com.demo.usermng.repository.LoginEventRepository;
import com.demo.usermng.repository.UserRepository;
import com.demo.usermng.repository.entity.LoginEventEntity;
import com.demo.usermng.repository.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class PlatformLoginEventService {

    private LoginEventRepository loginEventRepository;
    private PlatformUserService platformUserService;

    private final EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    public Collection<PlatformLoginEventDto> getLogEventsByRealmId(String realmId) {
        Collection<LoginEventEntity> logEventEntities = loginEventRepository.getLoginEventsByRealmId(realmId);

        Map<String, String> mappingToUserNames = platformUserService.getUserNamesMappedByUserIdForRealm(realmId);

        Collection<PlatformLoginEventDto> platformLoginEventCollection = new ArrayList<>();

        log.info("mappingToUserNames= " + mappingToUserNames);

        log.info("Login events entities BEFORE= " + logEventEntities);

        for (LoginEventEntity loginEventEntity : logEventEntities) {
            PlatformLoginEventDto platformLoginEventDto = mapper.fromLoginEventEntityToPlatformDto(loginEventEntity);
            platformLoginEventDto.setUserName(mappingToUserNames.get(loginEventEntity.getUserId()));
            platformLoginEventCollection.add(platformLoginEventDto);
        }

        log.info("Login events AFTER= " + platformLoginEventCollection);

        return platformLoginEventCollection;
    }

}
