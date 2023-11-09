package com.uberclone.userservice.controllers;


import com.uberclone.userservice.dtos.LoginRequestDTO;
import com.uberclone.userservice.dtos.LogoutRequestDTO;
import com.uberclone.userservice.dtos.SignUpRequestDTO;
import com.uberclone.userservice.exceptions.InvalidCredentialsException;
import com.uberclone.userservice.exceptions.SessionNotFoundException;
import com.uberclone.userservice.services.AuthService;
import com.uberclone.userservice.services.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    private AuthenticationManager authenticationManager;

    private JwtService jwtService;
    public AuthController(AuthService authService, AuthenticationManager authenticationManager,
                          JwtService jwtservice){

        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtservice;
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO requestDTO){
        try{
            Long id = authService.signUp(requestDTO.getName(), requestDTO.getEmail(), requestDTO.getPhoneNo(),
                    requestDTO.getPassword(), requestDTO.getUserRole());
            return new ResponseEntity<>("User Created: "+id, HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("Unable to create user", HttpStatus.CONFLICT);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> signIn(@RequestBody LoginRequestDTO loginRequestDTO){
        try{
//            String token = authService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPhoneNo(),
//                    loginRequestDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequestDTO.getPhoneNo() + ","+loginRequestDTO.getEmail(),
                    loginRequestDTO.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(loginRequestDTO.getPhoneNo(), loginRequestDTO.getEmail());
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                throw new InvalidCredentialsException("invalid user request !");
            }
        }
        catch (InvalidCredentialsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/logout")
//    @PreAuthorize("hasAnyAuthority('ROLE_DRIVER', 'ROLE_RIDER')")
    public ResponseEntity<String> logOut(@RequestBody LogoutRequestDTO requestDTO){
        try{
            authService.logOut(requestDTO.getUserId(), requestDTO.getToken());
            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
        }catch (SessionNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
