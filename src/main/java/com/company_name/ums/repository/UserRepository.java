package com.company_name.ums.repository;

import com.company_name.ums.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCode(String code);
    Optional<User> findByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.code = :code WHERE u.id = :id")
    void updateUserById(@Param("id")Long id, @Param("code") String code);
}
