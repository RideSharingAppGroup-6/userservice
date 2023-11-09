package com.uberclone.userservice.dtos;


import com.uberclone.userservice.models.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNo;
    private UserRole userRole;
}
