package com.sample.demoSample.service;

import com.sample.demoSample.dto.UserDto;
import com.sample.demoSample.dto.UserUpdateDto;
import com.sample.demoSample.util.error.UserAlreadyExistException;

import java.util.List;

public interface IUserService {

    UserUpdateDto registerNewOrdinaryUser(UserDto userDto) throws UserAlreadyExistException;

    UserUpdateDto findUserByEmail(String email);

    List<UserUpdateDto> listAllOrdinaryUser();

    void delete(long userId);

    UserUpdateDto update(UserUpdateDto userDto, long userId);

    UserUpdateDto get(long userId);
}
