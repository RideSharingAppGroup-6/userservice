package com.uberclone.userservice.services;

import com.uberclone.userservice.exceptions.InvalidCredentialsException;
import com.uberclone.userservice.exceptions.SessionNotFoundException;
import com.uberclone.userservice.models.Session;
import com.uberclone.userservice.models.SessionStatus;
import com.uberclone.userservice.models.User;
import com.uberclone.userservice.models.UserType;
import com.uberclone.userservice.repositories.SessionRepository;
import com.uberclone.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    @Value("${auth.token.expiry}")
    private int tokenExpiry;
    private final MacAlgorithm macAlgorithm = Jwts.SIG.HS256;
    private final SecretKey secretKey = macAlgorithm.key().build();
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Long signUp(String name, String email, String phoneNo, String password, UserType userType){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNo(phoneNo);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setUserType(userType);

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public String login(String email, String phoneNo, String password) throws InvalidCredentialsException {
        if(email!=null){
            Optional<User> userOptional = userRepository.findByEmail(email);
            if(userOptional.isEmpty()) throw new InvalidCredentialsException("Invalid Email");
            if(bCryptPasswordEncoder.matches(password, userOptional.get().getPassword())){
                return generateToken(userOptional.get());
            }else{
                throw new InvalidParameterException("Invalid Password");
            }

        } else if (phoneNo!=null) {
            Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);
            if(userOptional.isEmpty()) throw new InvalidCredentialsException("Invalid Phone no");
            if(bCryptPasswordEncoder.matches(password, userOptional.get().getPassword())){
                return generateToken(userOptional.get());
            }else{
                throw new InvalidCredentialsException("Invalid Password");
            }
        }
        return null;
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
    private String generateToken(User user){
//        secretKey = (SecretKey) "";
        Map<String, Object> jwtJson = new HashMap<>();
        jwtJson.put("id", user.getId());
        jwtJson.put("email", user.getEmail());
        jwtJson.put("userType", user.getUserType());
        jwtJson.put("createdAt", new Date());
        jwtJson.put("expiryAt", java.sql.Date.valueOf(LocalDate.now().plusDays(tokenExpiry-1)));
        String token = Jwts.builder().claims(jwtJson).signWith(secretKey).compact();
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);
        return token;
    }
    public boolean validate(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if(sessionOptional.isEmpty()) return false;
        Session session = sessionOptional.get();
        if(session.getSessionStatus().equals(SessionStatus.EXPIRED)) return false;
        if(!Jwts.parser().build().isSigned(token)) return false;
        Jws<Claims> claims = Jwts.parser().build().parseSignedClaims(token);
        Date expiredAt = (Date)claims.getPayload().get("expiryAt");
        if(expiredAt.before(new Date())) return false;
        UserType userType = (UserType) claims.getPayload().get("userType");
        return validateRoles(userType);
    }
    private boolean validateRoles(UserType userType){
        return true;
    }
}
