package com.codigo.code.test.service;

import com.codigo.code.test.dto.PackageDto;
import com.codigo.code.test.dto.request.PackagePurchaseRequest;
import com.codigo.code.test.dto.response.Response;

public interface PackageService {
     Response getAllPackages();
     Response getPackageById(Long id);
     Response createPackage(PackageDto packageDto);
     Response purchasePackage(PackagePurchaseRequest req);
}
