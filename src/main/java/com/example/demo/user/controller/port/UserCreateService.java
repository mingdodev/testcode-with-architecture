package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;

public interface UserCreateService {
    User create(UserCreate userCreate);
}
