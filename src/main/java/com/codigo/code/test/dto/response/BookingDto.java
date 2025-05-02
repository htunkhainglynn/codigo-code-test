package com.codigo.code.test.dto.response;

import com.codigo.code.test.entity.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDto {
    private Long id;

    private String username;

    private String courseName;

    private BookingStatus status;

    private LocalDateTime bookingDate;

    private String countryCode;

    public BookingDto(Booking booking) {
        this.id = booking.getId();
        this.username = booking.getUser().getUsername();
        this.courseName = booking.getCourse().getName();
        this.status = booking.getStatus();
        this.bookingDate = booking.getBookingDate();
        this.countryCode = booking.getCountry().getCountryCode();
    }
}
