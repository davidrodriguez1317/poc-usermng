package com.demo.usermng.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class PlatformLoginEventDto {
    private String userName;
    private String realmId;
    private String clientId;
    private String operation;
    private Date logTime;
}
