package com.codigo.code.test.scheduler;

import com.codigo.code.test.entity.Booking;
import com.codigo.code.test.entity.BookingStatus;
import com.codigo.code.test.entity.UserCredit;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.repo.BookingRepository;
import com.codigo.code.test.repo.UserCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final BookingRepository bookingRepo;
    private final UserCreditRepository userCreditRepo;

    @Scheduled(cron = "0 0 0 * * *")  // runs every midnight
    @Transactional
    public void refundCredits() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> pendingBookings = bookingRepo.findPendingBookingsAfterEndTime(now, BookingStatus.PENDING);
        log.info("Refunding credits for {} pending bookings", pendingBookings.size());

        for (Booking booking : pendingBookings) {
            UserCredit userCredit = userCreditRepo.findByUserId(booking.getUser().getId())
                    .orElseThrow(() -> new ApplicationException("User Credit not found", HttpStatus.NOT_FOUND));

            userCredit.setRemainingCredits(userCredit.getRemainingCredits() + booking.getCourse().getCredit());
            userCreditRepo.save(userCredit);

            booking.setStatus(BookingStatus.REFUNDED);
            bookingRepo.save(booking);
        }
    }
}
