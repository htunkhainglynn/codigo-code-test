package com.codigo.code.test.repo;

import com.codigo.code.test.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long>, JpaSpecificationExecutor<Country> {
    Optional<Object> findByCountryCode(String s);
}
