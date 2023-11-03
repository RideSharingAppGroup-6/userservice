package com.uberclone.userservice.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends BaseModel{
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String phoneNo;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private UserType userType;
}
