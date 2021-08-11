package com.demo.usermng.identityprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class IdpUserEventDto {
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String realmId;
    private String clientId;
    private String operation;
}
