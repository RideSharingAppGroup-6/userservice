package com.uberclone.userservice.services;

import com.uberclone.userservice.exceptions.InvalidCredentialsException;
import com.uberclone.userservice.exceptions.SessionNotFoundException;
import com.uberclone.userservice.models.Session;
import com.uberclone.userservice.models.SessionStatus;
import com.uberclone.userservice.models.User;
import com.uberclone.userservice.models.UserRole;
import com.uberclone.userservice.repositories.SessionRepository;
import com.uberclone.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository,
                       PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long signUp(String name, String email, String phoneNo, String password, UserRole userRole){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNo(phoneNo);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(userRole);

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public void logOut(Long userId, String token) throws SessionNotFoundException {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if(sessionOptional.isEmpty() || !sessionOptional.get().getSessionStatus().equals(SessionStatus.ACTIVE)){
            throw new SessionNotFoundException("No active sessions found for user: "+userId);
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.EXPIRED);
        sessionRepository.save(session);
    }
}
