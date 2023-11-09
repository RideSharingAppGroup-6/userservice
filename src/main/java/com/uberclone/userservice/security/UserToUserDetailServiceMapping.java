package com.uberclone.userservice.security;

import com.uberclone.userservice.models.User;
import com.uberclone.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserToUserDetailServiceMapping implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private Optional<User> getUserDetail(String phoneNo, String email){
        if(phoneNo!=null && !phoneNo.equals("null")) return userRepository.findByPhoneNo(phoneNo);
        return userRepository.findByEmail(email);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userInfo;
        String[] usernames = username.split(",");
        if(usernames.length==1) userInfo = getUserDetail(null, usernames[0]);
        else userInfo = getUserDetail(usernames[0], usernames[1]);
        return userInfo.map(UserToUserDetailsMapping::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));

    }
}
