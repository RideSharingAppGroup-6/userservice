package com.uberclone.userservice.dtos;


import com.uberclone.userservice.models.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNo;
    private UserType userType;
}
