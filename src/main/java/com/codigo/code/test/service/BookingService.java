package com.codigo.code.test.service;

import com.codigo.code.test.dto.request.BookingCancelRequest;
import com.codigo.code.test.dto.request.BookingRequest;
import com.codigo.code.test.dto.response.Response;

public interface BookingService {
    Response book(BookingRequest bookingRequest);

    Response cancel(BookingCancelRequest cancelRequest);
}
