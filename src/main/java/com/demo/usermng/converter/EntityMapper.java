package com.demo.usermng.converter;

import com.demo.usermng.identityprovider.dto.IdpLoginEventDto;
import com.demo.usermng.identityprovider.dto.IdpUserEventDto;
import com.demo.usermng.identityprovider.messaging.message.PlatformUserMessage;
import com.demo.usermng.platform.dto.PlatformLoginEventDto;
import com.demo.usermng.platform.dto.PlatformUserDto;
import com.demo.usermng.repository.entity.LoginEventEntity;
import com.demo.usermng.repository.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface EntityMapper {
    UserEntity fromIdpUserEventDtoToEntity(IdpUserEventDto idpUserEventDto);

    PlatformUserMessage fromIdpUserEventDtoToMessage(IdpUserEventDto idpUserEventDto);

    PlatformUserDto fromUserEntityToPlatformDto(UserEntity userEntity);

    LoginEventEntity fromIdpLoginEventDtoToEntity(IdpLoginEventDto idpLoginEventDto);

    PlatformLoginEventDto fromLoginEventEntityToPlatformDto(LoginEventEntity loginEventEntity);
}
