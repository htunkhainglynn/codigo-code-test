package com.codigo.code.test.entity;

import com.codigo.code.test.dto.request.CourseRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int credit;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private int slot;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    public Course(CourseRequest req) {
        this.name = req.name();
        this.description = req.description();
        this.credit = req.credit();
        this.endDate = req.endDate();
        this.startTime = req.startTime();
        this.endTime = req.endTime();
        this.slot = req.slot();
    }
}
