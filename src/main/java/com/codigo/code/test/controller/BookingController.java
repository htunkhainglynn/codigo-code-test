package com.codigo.code.test.controller;

import com.codigo.code.test.dto.request.BookingCancelRequest;
import com.codigo.code.test.dto.request.BookingRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.service.impl.BookingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
     private final BookingServiceImpl bookingService;

     @PostMapping("/book")
     public ResponseEntity<Response> book(@RequestBody BookingRequest bookingRequest) {
         return ok(bookingService.book(bookingRequest));
     }

     @PostMapping("/cancel")
        public ResponseEntity<Response> cancel(@RequestBody BookingCancelRequest cancelRequest) {
            return ok(bookingService.cancel(cancelRequest));
        }
}
