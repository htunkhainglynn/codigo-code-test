package com.codigo.code.test.service.impl;

import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.entity.Country;
import com.codigo.code.test.repo.CountryRepository;
import com.codigo.code.test.service.CountryService;
import com.codigo.code.test.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    public Response createCountry(String countryCode, String countryName) {
        Country country = countryRepository.save(new Country(countryCode, countryName));
        return ResponseBuilder.newBuilder().withData(country)
                .withMessage("Country created successfully")
                .build();
    }
}
