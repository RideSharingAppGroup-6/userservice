package com.uberclone.userservice.controllers;


import com.uberclone.userservice.dtos.RoleUpdateDTO;
import com.uberclone.userservice.dtos.UserResponseDTO;
import com.uberclone.userservice.dtos.UserUpdateDTO;
import com.uberclone.userservice.exceptions.UserNotFoundException;
import com.uberclone.userservice.models.UserRole;
import com.uberclone.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserDetails(@PathVariable Long id){
        try {
            UserResponseDTO user = userService.getUser(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (UserNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserDetails(@PathVariable Long id, @RequestBody UserUpdateDTO updateDTO){
        try{
            UserResponseDTO user = userService.updateUser(id, updateDTO);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/role/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable long id, @RequestBody RoleUpdateDTO request){
        try{
            userService.updateRole(id, request.getUserRole());
            return new ResponseEntity<>("Role updated successfully", HttpStatus.OK);
        }catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
