package com.codigo.code.test.repo;

import com.codigo.code.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> getReferenceByUsername(String username);

    @Query("SELECT u.password FROM User u WHERE u.username = ?1")
    String getPassword(String username);

}