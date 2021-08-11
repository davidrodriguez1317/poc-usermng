package com.demo.usermng.identityprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class IdpLoginEventDto {
    private String userId;
    private String realmId;
    private String clientId;
    private String operation;
}
