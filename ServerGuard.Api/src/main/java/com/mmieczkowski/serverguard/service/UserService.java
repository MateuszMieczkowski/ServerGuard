package com.mmieczkowski.serverguard.service;

import com.mmieczkowski.serverguard.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    Optional<User> getLoggedInUser();
}
