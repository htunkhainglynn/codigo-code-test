package com.codigo.code.test.controller;

import com.codigo.code.test.dto.PackageDto;
import com.codigo.code.test.dto.request.PackagePurchaseRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.service.impl.PackageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/package")
@RequiredArgsConstructor
public class PackageController {

    private final PackageServiceImpl packageService;

    @GetMapping("/all")
    public ResponseEntity<Response> getAllPackages() {
        return ok(packageService.getAllPackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getPackageById(@PathVariable Long id) {
        return ok(packageService.getPackageById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createPackage(@RequestBody PackageDto packageDto) {
        return ok(packageService.createPackage(packageDto));
    }

    @PostMapping("/purchase")
    public ResponseEntity<Response> purchasePackage(@RequestBody PackagePurchaseRequest req) {
        return ok(packageService.purchasePackage(req));
    }
}
