package com.uberclone.userservice.controllers;


import com.uberclone.userservice.dtos.LoginRequestDTO;
import com.uberclone.userservice.dtos.LogoutRequestDTO;
import com.uberclone.userservice.dtos.SignUpRequestDTO;
import com.uberclone.userservice.dtos.ValidateRequestDTO;
import com.uberclone.userservice.exceptions.InvalidCredentialsException;
import com.uberclone.userservice.exceptions.SessionNotFoundException;
import com.uberclone.userservice.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO requestDTO){
        try{
            Long id = authService.signUp(requestDTO.getName(), requestDTO.getEmail(), requestDTO.getPhoneNo(),
                    requestDTO.getPassword(), requestDTO.getUserType());
            return new ResponseEntity<>("User Created: "+id, HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("Unable to create user", HttpStatus.CONFLICT);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> signIn(@RequestBody LoginRequestDTO loginRequestDTO){
        try{
            String token = authService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPhoneNo(),
                    loginRequestDTO.getPassword());
            MultiValueMapAdapter<String, String> header = new MultiValueMapAdapter<>(new HashMap<>());
            header.add(HttpHeaders.SET_COOKIE, "access-token: "+token);
            return new ResponseEntity<>(header, HttpStatus.OK);
        }
        catch (InvalidCredentialsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@RequestBody LogoutRequestDTO requestDTO){
        try{
            authService.logOut(requestDTO.getUserId(), requestDTO.getToken());
            return new ResponseEntity<>("Logged out successful", HttpStatus.OK);
        }catch (SessionNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                            @RequestHeader("userId") Long userId){
        Boolean status = authService.validate(token, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
