package com.sample.demoSample.persistence.dao;

import com.sample.demoSample.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(String name);

    @Override
    void delete(Role role);
}
