package com.codigo.code.test.controller;

import com.codigo.code.test.dto.request.CountryRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @PostMapping("/create")
    public ResponseEntity<Response> createCountry(@RequestBody @Validated CountryRequest countryRequest) {
        return ok(countryService.createCountry(countryRequest.countryCode(), countryRequest.countryName()));
    }
}
