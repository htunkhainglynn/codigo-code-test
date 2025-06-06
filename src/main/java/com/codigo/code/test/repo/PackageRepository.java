package com.codigo.code.test.repo;

import com.codigo.code.test.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PackageRepository extends JpaRepository<Package, Long>, JpaSpecificationExecutor<Package> {
}
