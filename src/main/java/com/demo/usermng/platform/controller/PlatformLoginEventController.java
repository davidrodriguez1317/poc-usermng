package com.demo.usermng.platform.controller;

import com.demo.usermng.exception.RealmHandlingException;
import com.demo.usermng.platform.dto.PlatformLoginEventDto;
import com.demo.usermng.platform.service.PlatformLoginEventService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("loginevent")
@Log4j2
@AllArgsConstructor
public class PlatformLoginEventController {

    private final PlatformLoginEventService platformLoginEventService;

    @GetMapping("all")
    public Collection<PlatformLoginEventDto> getAllLogEventsForRealm(HttpServletRequest request) {

        log.info("Call for login events");

        String realmId = request.getAttribute("realmId").toString();

        if (Objects.isNull(realmId) || realmId.isBlank()) {
            throw new RealmHandlingException("No realm in request for getting all login events");
        }

        return platformLoginEventService.getLogEventsByRealmId(realmId);
    }

}
