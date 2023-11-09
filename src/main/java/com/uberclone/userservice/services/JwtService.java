package com.uberclone.userservice.services;

import com.uberclone.userservice.models.Session;
import com.uberclone.userservice.models.SessionStatus;
import com.uberclone.userservice.models.User;
import com.uberclone.userservice.repositories.SessionRepository;
import com.uberclone.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    @Value("${auth.token.expiry.days}")
    private int tokenExpiry;

    @Value(("${auth.secret.key}"))
    private String secretKey;

//    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Email(token, userDetails.getUsername());
        if(sessionOptional.isEmpty()) return false;
        Session session = sessionOptional.get();
        if(session.getSessionStatus().equals(SessionStatus.EXPIRED)) return false;
        Boolean status = isTokenExpired(token);
        if(status){
            session.setSessionStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
        }
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public String generateToken(String phoneNo, String email){
        Optional<User> optionalUserInfo;
        if(phoneNo!=null) optionalUserInfo = userRepository.findByPhoneNo(phoneNo);
        else optionalUserInfo = userRepository.findByEmail(email);
        User user = optionalUserInfo.get();
        Map<String,Object> claims=new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("phoneNo", user.getPhoneNo());
        String token = createToken(claims,user.getEmail());
        Session session = new Session();
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        sessionRepository.save(session);
        return token;
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000L *60*60*24*tokenExpiry))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
