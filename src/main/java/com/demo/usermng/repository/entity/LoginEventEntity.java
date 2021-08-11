package com.demo.usermng.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginEventEntity {
    private String userId;
    private String realmId;
    private String clientId;
    private String operation;
    private Date logTime;
}
