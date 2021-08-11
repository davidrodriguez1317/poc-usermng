package com.demo.usermng.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformUserDto {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Date creationTime;
}
