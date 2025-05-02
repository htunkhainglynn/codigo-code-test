package com.codigo.code.test.repo;

import com.codigo.code.test.entity.Booking;
import com.codigo.code.test.entity.BookingStatus;
import com.codigo.code.test.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT b FROM Booking b WHERE b.user.username = ?1")
    List<Booking> findByUsername(String username);

    Optional<Booking> findFirstByCourseAndStatusOrderByBookingDateAsc(Course course, BookingStatus bookingStatus);

    Optional<Object> findByIdAndUsername(Long bookingId, String username);
}
