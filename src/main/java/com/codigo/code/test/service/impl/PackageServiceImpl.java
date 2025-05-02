package com.codigo.code.test.service.impl;

import com.codigo.code.test.dto.PackageDto;
import com.codigo.code.test.dto.request.PackagePurchaseRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.entity.Country;
import com.codigo.code.test.entity.Package;
import com.codigo.code.test.entity.UserCredit;
import com.codigo.code.test.entity.UserPackage;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.repo.*;
import com.codigo.code.test.service.PackageService;
import com.codigo.code.test.utils.ResponseBuilder;
import com.codigo.code.test.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final CountryRepository countryRepository;
    private final SecurityContextUtils securityContext;
    private final UserCreditRepository userCreditRepository;
    private final UserPackageRepository userPackageRepository;
    private final UserRepository userRepository;

    @Override
    public Response getAllPackages() {
        try {
            List<Package> packages = packageRepository.findAll();
            log.info("Retrieved {} packages", packages.size());

            List<PackageDto> packageDtos = packages.stream()
                    .map(packageEntity -> new PackageDto(
                            packageEntity.getId(),
                            packageEntity.getName(),
                            packageEntity.getCredit(),
                            packageEntity.getDescription(),
                            packageEntity.getExpiredDateCount(),
                            packageEntity.getCountry().getCountryCode()))
                    .toList();

            return ResponseBuilder
                    .newBuilder()
                    .withKey("packages")
                    .withData(packageDtos)
                    .withMessage("All packages retrieved successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving packages: {}", e.getMessage());
            throw new ApplicationException("Error retrieving packages", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response getPackageById(Long id) {
        Package packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Package not found", HttpStatus.NOT_FOUND));

        PackageDto packageDto = new PackageDto(
                packageEntity.getId(),
                packageEntity.getName(),
                packageEntity.getCredit(),
                packageEntity.getDescription(),
                packageEntity.getExpiredDateCount(),
                packageEntity.getCountry().getCountryCode());

        return ResponseBuilder
                .newBuilder()
                .withKey("package")
                .withData(packageDto)
                .withMessage("Package retrieved successfully")
                .build();
    }

    @Override
    public Response createPackage(PackageDto packageDto) {
        Package packageEntity = new Package();
        packageEntity.setName(packageDto.name());
        packageEntity.setCredit(packageDto.credit());
        packageEntity.setDescription(packageDto.description());
        packageEntity.setExpiredDateCount(packageDto.expiredDateCount());
        packageEntity.setCountry((Country) countryRepository.findByCountryCode(packageDto.countryCode())
                .orElseThrow(() -> new ApplicationException("Country not found", HttpStatus.NOT_FOUND)));

        try{
            Package savedPackage = packageRepository.save(packageEntity);

            return ResponseBuilder
                    .newBuilder()
                    .withData(new PackageDto(
                            savedPackage.getId(),
                            savedPackage.getName(),
                            savedPackage.getCredit(),
                            savedPackage.getDescription(),
                            savedPackage.getExpiredDateCount(),
                            savedPackage.getCountry().getCountryCode()))
                    .withMessage("Package created successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error creating package: {}", e.getMessage());
            throw new ApplicationException("Error creating package", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public Response purchasePackage(PackagePurchaseRequest req) {

        String username = securityContext.getUsername();
        Package packageEntity = packageRepository.findById(req.id())
                .orElseThrow(() -> new ApplicationException("Package not found", HttpStatus.NOT_FOUND));

        UserCredit userCredit = userCreditRepository.findByUsernameAndCountryCode(
                username, packageEntity.getCountry().getCountryCode());

        if (userCredit == null) {
            UserCredit newUserCredit = new UserCredit();
            newUserCredit.setUser(userRepository.getReferenceByUsername(username)
                    .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND)));
            newUserCredit.setCountry(packageEntity.getCountry());
            newUserCredit.setRemainingCredits(packageEntity.getCredit());
            newUserCredit.setExpiredDate(LocalDate.now().plusDays(packageEntity.getExpiredDateCount()));
            userCreditRepository.save(newUserCredit);
        } else {
            userCredit.setRemainingCredits(userCredit.getRemainingCredits() + packageEntity.getCredit());
            userCredit.setExpiredDate(userCredit.getExpiredDate().plusDays(packageEntity.getExpiredDateCount()));
            userCreditRepository.save(userCredit);
        }

        UserPackage userPackage = new UserPackage();
        userPackage.setUser(userRepository.getReferenceByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND)));
        userPackage.setPkg(packageEntity);

        userPackageRepository.save(userPackage);

        return ResponseBuilder
                .newBuilder()
                .withData(PackageDto.builder()
                        .id(packageEntity.getId())
                        .name(packageEntity.getName())
                        .credit(packageEntity.getCredit())
                        .description(packageEntity.getDescription())
                        .expiredDateCount(packageEntity.getExpiredDateCount())
                        .countryCode(packageEntity.getCountry().getCountryCode())
                        .build())
                .withMessage("Package created successfully")
                .build();
    }
}
