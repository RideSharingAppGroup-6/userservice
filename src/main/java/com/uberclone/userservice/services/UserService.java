package com.uberclone.userservice.services;

import com.uberclone.userservice.dtos.UserResponseDTO;
import com.uberclone.userservice.dtos.UserUpdateDTO;
import com.uberclone.userservice.exceptions.UserNotFoundException;
import com.uberclone.userservice.models.User;
import com.uberclone.userservice.models.UserRole;
import com.uberclone.userservice.repositories.UserRepository;
import org.hibernate.usertype.UserType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO getUser(Long userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User with id "+userId+" does not exist");
        UserResponseDTO user = new UserResponseDTO();
        user.setName(userOptional.get().getName());
        user.setEmail(userOptional.get().getEmail());
        user.setPhoneNo(userOptional.get().getPhoneNo());
        user.setUserRole(userOptional.get().getUserRole());

        return user;
    }
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO requestDTO) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User with id "+userId+" does not exist");
        User user = userOptional.get();
        if(requestDTO.getName()!=null) user.setName(requestDTO.getName());
//        if(requestDTO.getUserRole()!=null) user.setUserRole(requestDTO.getUserRole());
        if(requestDTO.getPassword()!=null) user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        if(requestDTO.getEmail()!=null) user.setEmail(requestDTO.getEmail());
        if(requestDTO.getPhoneNo()!=null) user.setPhoneNo(requestDTO.getPhoneNo());
        User savedUser = userRepository.save(user);
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setPhoneNo(savedUser.getPhoneNo());
        responseDTO.setUserRole(savedUser.getUserRole());
        return responseDTO;
    }
    public void updateRole(Long userId, UserRole role) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User with id "+userId+" does not exist");
        User user = userOptional.get();
        user.setUserRole(role);
        userRepository.save(user);
    }
}
