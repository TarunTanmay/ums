package com.company_name.ums.repository;

import com.company_name.ums.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String name);
}
