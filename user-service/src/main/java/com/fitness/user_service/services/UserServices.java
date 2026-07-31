package com.fitness.user_service.services;

import com.fitness.user_service.dtos.user.RegisterRequest;
import com.fitness.user_service.dtos.user.UserDto;
import com.fitness.user_service.exceptions.custom.BadRequestException;
import com.fitness.user_service.models.User;
import com.fitness.user_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserDto registerUser(RegisterRequest request) {
        String email = request.getEmail();
        String name = request.getName();
        String password = request.getPassword();
        String keyCloakId = request.getKeyCloakId();
        if(userRepository.existsByEmail(email)){
            throw new BadRequestException("User exist with email: " + email);
        }
//        create hash password;
        String hashPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(hashPassword);
        newUser.setName(name);
        newUser.setKeyCloakId(keyCloakId);

        User savedUser = userRepository.save(newUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new BadRequestException("Invalid email :"+email);
        }
        return modelMapper.map(user, UserDto.class);
    }
}
