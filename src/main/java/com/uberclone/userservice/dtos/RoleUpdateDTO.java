package com.uberclone.userservice.dtos;

import com.uberclone.userservice.models.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateDTO {
    private UserRole userRole;
}
