package com.uberclone.userservice.dtos;

import com.uberclone.userservice.models.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String name;
    private String email;
    private String phoneNo;
    private UserType userType;
    private String password;
}
