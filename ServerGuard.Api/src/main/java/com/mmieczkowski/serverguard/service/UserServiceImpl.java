package com.mmieczkowski.serverguard.service;

import com.mmieczkowski.serverguard.user.User;
import com.mmieczkowski.serverguard.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getLoggedInUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(jwt == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(jwt.getSubject());
    }
}
