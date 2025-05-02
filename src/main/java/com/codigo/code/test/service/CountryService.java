package com.codigo.code.test.service;

import com.codigo.code.test.dto.response.Response;

public interface CountryService {
    Response createCountry(String countryCode, String countryName);
}
