package com.sample.demoSample.web.controller;

import com.sample.demoSample.persistence.dao.RoleRepository;
import com.sample.demoSample.persistence.dao.UserRepository;
import com.sample.demoSample.service.IUserService;
import com.sample.demoSample.dto.UserDto;
import com.sample.demoSample.dto.UserUpdateDto;
import com.sample.demoSample.persistence.model.User;
import com.sample.demoSample.util.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Save new User with Role User
     * @param userDto
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseBody
    public GenericResponse registerUserAccount(@Valid @RequestBody UserDto userDto, final HttpServletRequest request) {
        userService.registerNewOrdinaryUser(userDto);
        return new GenericResponse(messages.getMessage("user.register.success", null, request.getLocale()));
    }

    /**
     * List all Users (List only User Role Users)
     * @return
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @ResponseBody
    public List<UserUpdateDto> allUsersList() {
        return userService.listAllOrdinaryUser();
    }

    /**
     * Get Details of the user by Id
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public UserUpdateDto get(@PathVariable long userId) {
        return userService.get(userId);
    }

    /**
     * Update User with details and id
     * @param userDto
     * @param userId
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public UserUpdateDto update(@Valid @RequestBody UserUpdateDto userDto, @PathVariable long userId, final HttpServletRequest request) {
        User user = userRepository.findUserByEmail(request.getUserPrincipal().getName());
        if (user != null && (user.getId().equals(userId) || user.getRoles().contains(roleRepository.findRoleByName("ROLE_ADMIN")))) {
            return userService.update(userDto, userId);
        } else {
            throw new AccessDeniedException("");
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/byEmail", method = RequestMethod.GET)
    @ResponseBody
    public UserUpdateDto findByEmail(@RequestParam(value = "email") String email, final HttpServletRequest request) {
        return userService.findUserByEmail(email);
    }

    /**
     * Delete User with Id
     * @param userId
     * @param request
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public GenericResponse deleteUser(@PathVariable long userId, final HttpServletRequest request) {
        User user = userRepository.findUserByEmail(request.getUserPrincipal().getName());
        if (user != null && user.getRoles().contains(roleRepository.findRoleByName("ROLE_ADMIN"))) {
            userService.delete(userId);
            return new GenericResponse(messages.getMessage("user.delete.success", null, request.getLocale()));
        } else {
            throw new AccessDeniedException("");
        }
    }
}
