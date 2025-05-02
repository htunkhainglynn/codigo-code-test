package com.codigo.code.test.repo;

import com.codigo.code.test.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserPackageRepository extends JpaRepository<UserPackage, Long>, JpaSpecificationExecutor<UserPackage> {
}
