package com.demo.usermng.identityprovider.controller;

import com.demo.usermng.identityprovider.dto.IdpUserEventDto;
import com.demo.usermng.identityprovider.dto.IdpLoginEventDto;
import com.demo.usermng.identityprovider.service.IdpLoginEventService;
import com.demo.usermng.identityprovider.service.IdpUserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@Log4j2
@RestController
@RequestMapping("idp")
@AllArgsConstructor
public class IdpController {

    private IdpUserService idpUserService;
    private IdpLoginEventService idpLoginEventService;

    @PostMapping("user")
    public Response addUserFromIdp(@RequestBody IdpUserEventDto idpUserEvent) {
        log.info("User CRUD from Idp -> " + idpUserEvent);

        idpUserService.handleUserEvent(idpUserEvent);

        return Response.ok().build();
    }

    @PostMapping("log")
    public Response logUserAction(@RequestBody IdpLoginEventDto idpLoginEventDto) {
        log.info("Log from Idp -> " + idpLoginEventDto);

        idpLoginEventService.handleLoginEvent(idpLoginEventDto);

        return Response.ok().build();
    }

}
