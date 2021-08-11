package com.demo.usermng.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserEntity {
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String realmId;
    private String clientId;
    private Date creationTime;
    private Date modificationTime;
}
