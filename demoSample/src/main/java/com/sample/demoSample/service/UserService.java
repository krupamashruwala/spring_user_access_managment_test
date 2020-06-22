package com.sample.demoSample.service;

import com.sample.demoSample.dto.UserDto;
import com.sample.demoSample.dto.UserUpdateDto;
import com.sample.demoSample.persistence.dao.RoleRepository;
import com.sample.demoSample.persistence.dao.UserRepository;
import com.sample.demoSample.persistence.model.Role;
import com.sample.demoSample.persistence.model.User;
import com.sample.demoSample.util.error.EmailUserUpdateAlreadyExistException;
import com.sample.demoSample.util.error.ResourceNotFoundException;
import com.sample.demoSample.util.error.UserAlreadyExistException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {
    PropertyMap<UserDto, User> skipAuditFieldsMap = new PropertyMap<UserDto, User>() {
        protected void configure() {
            skip().setId(null);
            skip().setPassword(null);
            skip().setEnabled(false);
            skip().setRoles(null);
        }
    };

    PropertyMap<UserUpdateDto, User> skipAuditFieldsUserUpdateDtoMap = new PropertyMap<UserUpdateDto, User>() {
        protected void configure() {
            skip().setId(null);
            skip().setPassword(null);
            skip().setEnabled(false);
            skip().setRoles(null);
        }
    };

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    private ModelMapper modelMapper;
    private ModelMapper userUpdateModelMapper;

    @Autowired
    public UserService(ModelMapper modelMapper, ModelMapper userUpdateModelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.addMappings(skipAuditFieldsMap);
        this.userUpdateModelMapper = userUpdateModelMapper;
        this.userUpdateModelMapper.addMappings(skipAuditFieldsUserUpdateDtoMap);
    }

    private boolean emailExist(final String email) {
        return userRepository.findUserByEmail(email) != null;
    }

    @Override
    public List<UserUpdateDto> listAllOrdinaryUser() {
        Collection<Role> roles = Arrays.asList(roleRepository.findRoleByName("ROLE_USER"));
        List<User> users = userRepository.findAllByRolesIsIn(roles);
        return users.stream()
                .filter(e -> !e.isDeleted())
                .map(user -> convertToUserUpdateDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserUpdateDto registerNewOrdinaryUser(final UserDto userDto) throws UserAlreadyExistException {
        if (emailExist(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        final User user = new User();
        if (userDto.getPassword() != null) user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(true);
        user.setRoles(Arrays.asList(roleRepository.findRoleByName("ROLE_USER")));
        return convertToUserUpdateDto(userRepository.save(convertToEntity(userDto, user)));
    }

    @Override
    public UserUpdateDto findUserByEmail(String email) {
        return convertToUserUpdateDto(userRepository.findUserByEmail(email));
    }

    @Override
    public void delete(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setEmail(user.get().getEmail() + " DELETED " + (Math.random() * 1000000L));
            user.get().setDeleted(true);
            userRepository.save(user.get());
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }

    @Override
    public UserUpdateDto update(UserUpdateDto userDto, long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User emailUser = userRepository.findUserByEmail(userDto.getEmail());
            if (emailUser.getId() != userId) {
                throw new EmailUserUpdateAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
            }
            return convertToUserUpdateDto(userRepository.save(convertToEntity(userDto, user.get())));
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }

    @Override
    public UserUpdateDto get(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return convertToUserUpdateDto(user.get());
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }

    private User convertToEntity(UserDto userDto, User user) throws ParseException {
        if (user == null) {
            user = modelMapper.map(userDto, User.class);
        } else {
            Long id = user.getId();
            String password = user.getPassword();
            boolean enabled = user.isEnabled();
            Collection<Role> roles = user.getRoles();
            modelMapper.map(userDto, user);
            user.setId(id);
            user.setPassword(password);
            user.setEnabled(enabled);
            user.setRoles(roles);
        }

        return user;
    }

    private User convertToEntity(UserUpdateDto userDto, User user) throws ParseException {
        if (user == null) {
            user = modelMapper.map(userDto, User.class);
        } else {
            Long id = user.getId();
            String password = user.getPassword();
            boolean enabled = user.isEnabled();
            Collection<Role> roles = user.getRoles();
            modelMapper.map(userDto, user);
            user.setId(id);
            user.setPassword(password);
            user.setEnabled(enabled);
            user.setRoles(roles);
        }
        return user;
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return userDto;
    }

    private UserUpdateDto convertToUserUpdateDto(User user) {
        UserUpdateDto userDto = userUpdateModelMapper.map(user, UserUpdateDto.class);
        return userDto;
    }
}
