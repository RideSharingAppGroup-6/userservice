package com.uberclone.userservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> getHealthCheckResponse(){
        try{
            return new ResponseEntity<>("User Service is up and running", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("User Service is down", HttpStatus.GATEWAY_TIMEOUT);
        }
    }
}
