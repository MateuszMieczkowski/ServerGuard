package com.mmieczkowski.serverguard.service;

import com.mmieczkowski.serverguard.user.model.User;
import com.mmieczkowski.serverguard.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Service
@RequestScope
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;
    private User cachedUser;

    public CurrentUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public synchronized Optional<User> getLoggedInUser() {
        if(cachedUser != null) {
            return Optional.of(cachedUser);
        }
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(jwt == null) {
            return Optional.empty();
        }
        var optionalUser = userRepository.findByEmail(jwt.getSubject());
        optionalUser.ifPresent(user -> cachedUser = user);
        return optionalUser;
    }
}
