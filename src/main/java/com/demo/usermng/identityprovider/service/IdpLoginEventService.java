package com.demo.usermng.identityprovider.service;

import com.demo.usermng.converter.EntityMapper;
import com.demo.usermng.identityprovider.dto.IdpLoginEventDto;
import com.demo.usermng.repository.LoginEventRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class IdpLoginEventService {

    private final LoginEventRepository loginEventRepository;

    private final EntityMapper mapper = Mappers.getMapper(EntityMapper.class);

    @Autowired
    public IdpLoginEventService(
            @Qualifier("loginEventRepository") LoginEventRepository loginEventRepository) {
        this.loginEventRepository = loginEventRepository;
    }

    public void handleLoginEvent(IdpLoginEventDto logEventDto) {
        loginEventRepository.addLoginEvent(mapper.fromIdpLoginEventDtoToEntity(logEventDto));
    }


}
