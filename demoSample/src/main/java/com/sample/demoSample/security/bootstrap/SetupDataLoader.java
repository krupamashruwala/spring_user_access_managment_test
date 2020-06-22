package com.sample.demoSample.security.bootstrap;

import com.sample.demoSample.persistence.dao.PrivilegeRepository;
import com.sample.demoSample.persistence.dao.RoleRepository;
import com.sample.demoSample.persistence.dao.UserRepository;
import com.sample.demoSample.persistence.model.Privilege;
import com.sample.demoSample.persistence.model.Role;
import com.sample.demoSample.persistence.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
@Profile("!testing")
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetupDataLoader.class);

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        if (alreadySetup) return;

        final Privilege userPrivilege = createPrivilegeIfNotFound("USER");
        final Privilege adminPrivilege = createPrivilegeIfNotFound("ADMIN");

        final List<Privilege> adminPrivileges = new ArrayList<Privilege>(
                Arrays.asList(
                        userPrivilege,
                        adminPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<Privilege>(
                Arrays.asList(
                        userPrivilege));

        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        final Role userRole = createRoleIfNotFound("ROLE_USER", userPrivileges);

        createUserIfNotFound(
                "superuser@gmail.com",
                "Super",
                "User",
                "bgeUXQDbebirDMykH3n66KH7",
                new ArrayList<Role>(Arrays.asList(adminRole)), passwordEncoder, userRepository);
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findPrivilegeByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findRoleByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            role = roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    public User createUserIfNotFound(final String email,
                                     final String firstName,
                                     final String lastName,
                                     final String password,
                                     final Collection<Role> roles, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }
}
