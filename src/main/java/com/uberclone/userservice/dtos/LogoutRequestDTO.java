package com.uberclone.userservice.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {
    private Long userId;
    private String token;
}
