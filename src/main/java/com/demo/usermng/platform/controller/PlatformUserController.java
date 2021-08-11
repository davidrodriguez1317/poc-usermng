package com.demo.usermng.platform.controller;

import com.demo.usermng.exception.RealmHandlingException;
import com.demo.usermng.platform.dto.PlatformUserDto;
import com.demo.usermng.platform.service.PlatformUserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("user")
@Log4j2
@AllArgsConstructor
public class PlatformUserController {

    private final PlatformUserService platformUserService;

    @GetMapping("all")
    public Collection<PlatformUserDto> getAllUsersForRealm(HttpServletRequest request) {

        log.info("Call for users");

        String realmId = request.getAttribute("realmId").toString();

        if (Objects.isNull(realmId) || realmId.isBlank()) {
            throw new RealmHandlingException("No realm in request for getting all users");
        }

        return platformUserService.getUsersByRealmId(realmId);
    }

}
