package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    void updateUser(User user);

    User findByEmail(String email);

    User findByContact(String contactNo);

    List<UserDto> findAllUsers();
}
