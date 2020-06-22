package com.sample.demoSample.persistence.dao;

import com.sample.demoSample.persistence.model.Role;
import com.sample.demoSample.persistence.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = :email")
    User findUserByEmail(@Param("email") String email);

    List<User> findAllByRolesIsIn(Collection<Role> roles);

    @Query("select u FROM User u LEFT JOIN u.roles r WHERE u.deleted = false AND u.createdDate >= :dateFrom AND u.createdDate <= :dateTo AND r IN :roles")
    Slice<User> findAllByRolesIsIn(@Param("roles") Collection<Role> roles, Pageable pageable, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("select u FROM User u LEFT JOIN u.roles r WHERE u.deleted = false AND u.createdDate >= :dateFrom AND u.createdDate <= :dateTo AND r NOT IN :roles")
    List<User> findAll(@Param("roles") Collection<Role> roles, Pageable pageable, @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Override
    void delete(User user);
}
