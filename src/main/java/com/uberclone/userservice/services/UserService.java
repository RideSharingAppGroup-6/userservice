package com.uberclone.userservice.services;

import com.uberclone.userservice.dtos.UserResponseDTO;
import com.uberclone.userservice.dtos.UserUpdateDTO;
import com.uberclone.userservice.exceptions.UserNotFoundException;
import com.uberclone.userservice.models.User;
import com.uberclone.userservice.repositories.SessionRepository;
import com.uberclone.userservice.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserResponseDTO getUser(Long userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User with id "+userId+" does not exist");
        UserResponseDTO user = new UserResponseDTO();
        user.setName(userOptional.get().getName());
        user.setEmail(userOptional.get().getEmail());
        user.setPhoneNo(userOptional.get().getPhoneNo());
        user.setUserType(userOptional.get().getUserType());

        return user;
    }
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO requestDTO) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) throw new UserNotFoundException("User with id "+userId+" does not exist");
        User user = userOptional.get();
        if(requestDTO.getName()!=null) user.setName(requestDTO.getName());
        if(requestDTO.getUserType()!=null) user.setUserType(requestDTO.getUserType());
        if(requestDTO.getPassword()!=null) user.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        if(requestDTO.getEmail()!=null) user.setEmail(requestDTO.getEmail());
        if(requestDTO.getPhoneNo()!=null) user.setPhoneNo(requestDTO.getPhoneNo());
        User savedUser = userRepository.save(user);
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setPhoneNo(savedUser.getPhoneNo());
        responseDTO.setUserType(savedUser.getUserType());
        return responseDTO;
    }
}
