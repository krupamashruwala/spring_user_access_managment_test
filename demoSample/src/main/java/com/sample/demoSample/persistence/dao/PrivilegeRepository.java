package com.sample.demoSample.persistence.dao;

import com.sample.demoSample.persistence.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long>{
    Privilege findPrivilegeByName(String name);

    @Override
    void delete(Privilege privilege);
}
