package com.codigo.code.test.repo;

import com.codigo.code.test.entity.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCreditRepository extends JpaRepository<UserCredit, Long>, JpaSpecificationExecutor<UserCredit> {

    @Query("SELECT uc FROM UserCredit uc WHERE uc.user.username = ?1 and uc.country.countryCode = ?2")
    Optional<UserCredit> findByUsernameAndCountryCode(String username, String countryCode);

    @Query("SELECT uc FROM UserCredit uc WHERE uc.user.id = ?1")
    Optional<UserCredit> findByUserId(Long id);
}
