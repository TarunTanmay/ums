package com.company_name.ums.repository;

import com.company_name.ums.model.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    @Transactional
    @Modifying
    @Query("select u from UserLogin u WHERE u.expired = false")
    List<UserLogin> findNonExpiredToken();
    Optional<UserLogin> findByToken(String token);
}
