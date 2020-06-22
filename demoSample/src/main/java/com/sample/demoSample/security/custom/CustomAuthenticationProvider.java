package com.sample.demoSample.security.custom;

import com.sample.demoSample.persistence.dao.UserRepository;
import com.sample.demoSample.persistence.model.Role;
import com.sample.demoSample.persistence.model.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collection;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final User user = userRepository.findUserByEmail(authentication.getName());
        if ((user == null)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        final Authentication result = super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(user, result.getCredentials(), result.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean isAdminAccount(User user) {
        Collection<Role> roles = user.getRoles();
        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase("admin")) {
                return true;
            }
        }

        return false;
    }
}
