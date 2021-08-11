package com.demo.usermng.identityprovider.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class PlatformUserMessage {
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String operation;
}
