package com.codigo.code.test.service.impl;

import com.codigo.code.test.dto.request.BookingCancelRequest;
import com.codigo.code.test.dto.request.BookingRequest;
import com.codigo.code.test.dto.response.BookingDto;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.entity.Booking;
import com.codigo.code.test.entity.BookingStatus;
import com.codigo.code.test.entity.Course;
import com.codigo.code.test.entity.UserCredit;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.repo.BookingRepository;
import com.codigo.code.test.repo.CourseRepository;
import com.codigo.code.test.repo.UserCreditRepository;
import com.codigo.code.test.repo.UserRepository;
import com.codigo.code.test.service.BookingService;
import com.codigo.code.test.utils.ResponseBuilder;
import com.codigo.code.test.utils.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final CourseRepository courseRepo;
    private final UserCreditRepository userCreditRepo;
    private final SecurityContextUtils securityContext;
    private final UserRepository userRepo;
    private final RedisService redisService;

    @Transactional
    @Override
    public Response book(BookingRequest bookingRequest) {

            // Fetch course
            Course course = courseRepo.findById(bookingRequest.courseId())
                    .orElseThrow(() -> new ApplicationException("Course not found", HttpStatus.NOT_FOUND));

            String username = securityContext.getUsername();

            // 1. Check credit for the course's country
            String countryCode = course.getCountry().getCountryCode();

            UserCredit userCredit = userCreditRepo.findByUsernameAndCountryCode(username, countryCode).orElseThrow(
                    () -> new ApplicationException("User credit not found", HttpStatus.NOT_FOUND));

            if (userCredit.getRemainingCredits() < course.getCredit()) {
                throw new ApplicationException("Insufficient credits", HttpStatus.BAD_REQUEST);
            }

            if (userCredit.getExpiredDate().isBefore(LocalDate.now())) {
                throw new ApplicationException("Credit expired", HttpStatus.BAD_REQUEST);
            }

            // 2. Check for time conflict
            List<Booking> userBookings = bookingRepo.findByUsername(username);
            for (Booking b : userBookings) {
                Course existingCourse = b.getCourse();
                boolean overlaps = timeOverlap(
                        course.getStartTime(), course.getEndTime(),
                        existingCourse.getStartTime(), existingCourse.getEndTime()
                );
                if (overlaps) {
                    throw new ApplicationException("Booking time conflicts with another class", HttpStatus.BAD_REQUEST);
                }
            }

            Response response;

            // 3. use redis to handle concurrency
            String status = redisService.checkSlot(course.getId().toString());
            try{
                if (BookingStatus.BOOKED.toString().equalsIgnoreCase(status)) {
                    course.setSlot(course.getSlot() - 1);
                    courseRepo.save(course);
                    response = saveBooking(username, course, BookingStatus.BOOKED);
                } else {
                    response = saveBooking(username, course, BookingStatus.PENDING);
                }

                // 4. Decrease credit
                userCredit.setRemainingCredits(userCredit.getRemainingCredits() - course.getCredit());
                userCreditRepo.save(userCredit);

                return response;

        } catch (Exception e) {
            throw new ApplicationException("Error occurred while booking", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Response cancel(BookingCancelRequest cancelRequest) {
        try {
            String username = securityContext.getUsername();
            Long bookingId = cancelRequest.bookingId();

            Booking booking = bookingRepo.findByIdAndUsername(bookingId, username)
                    .orElseThrow(() -> new ApplicationException("Booking not found", HttpStatus.NOT_FOUND));

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                throw new ApplicationException("Booking already canceled", HttpStatus.BAD_REQUEST);
            }

            // Refund logic (only within 4 hours)
            if (Duration.between(booking.getBookingDate(), LocalDateTime.now()).toHours() <= 4
                    && (booking.getStatus() == BookingStatus.BOOKED || booking.getStatus() == BookingStatus.PENDING)) {

                Course course = booking.getCourse();
                String countryCode = course.getCountry().getCountryCode();

                UserCredit userCredit = userCreditRepo.findByUsernameAndCountryCode(username, countryCode).orElseThrow(
                        () -> new ApplicationException("User credit not found", HttpStatus.NOT_FOUND));
                userCredit.setRemainingCredits(userCredit.getRemainingCredits() + course.getCredit());
                userCreditRepo.save(userCredit);
            }

            booking.setStatus(BookingStatus.CANCELLED);
            booking.setModifiedDate(LocalDateTime.now());
            bookingRepo.save(booking);

            // Promote the earliest pending booking
            Course course = booking.getCourse();
            Optional<Booking> pendingBooking = bookingRepo
                    .findFirstByCourseAndStatusOrderByBookingDateAsc(course, BookingStatus.PENDING);

            if (pendingBooking.isPresent()) {
                Booking toPromote = pendingBooking.get();
                toPromote.setStatus(BookingStatus.BOOKED);
                toPromote.setModifiedDate(LocalDateTime.now());
                bookingRepo.save(toPromote);
            }

            return ResponseBuilder.newBuilder()
                    .withMessage("Booking canceled successfully")
                    .withHttpStatus(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            throw new ApplicationException("Error occurred while canceling booking", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private Response saveBooking(String username, Course course, BookingStatus saveBooking) {
        Booking booking = Booking.builder()
                .user(userRepo.getReferenceByUsername(username)
                        .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND)))
                .course(course)
                .status(saveBooking)
                .bookingDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .country(course.getCountry())
                .build();

        booking = bookingRepo.save(booking);

        return ResponseBuilder.newBuilder()
                .withData(new BookingDto(booking))
                .withMessage("Course booked successfully").build();
    }

    private boolean timeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

}
