package com.example.demo.user.service.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long id);

    Optional<User> findByEmailAndStatus(String email, UserStatus userStatus);

    Optional<User> findByIdAndStatus(long id, UserStatus userStatus);

    User save(User user);

    User getById(long id);
}
