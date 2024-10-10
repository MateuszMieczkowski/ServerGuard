package com.mmieczkowski.serverguard.service;

import com.mmieczkowski.serverguard.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    Optional<User> getLoggedInUser();
}
