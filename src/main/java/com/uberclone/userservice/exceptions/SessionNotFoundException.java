package com.uberclone.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SessionNotFoundException extends Exception{
    public SessionNotFoundException(String message){
        super(message);
    }
}
