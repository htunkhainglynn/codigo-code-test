package com.codigo.code.test.service.impl;

import com.codigo.code.test.dto.request.CourseRequest;
import com.codigo.code.test.dto.response.CourseDto;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.entity.Country;
import com.codigo.code.test.entity.Course;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.repo.CountryRepository;
import com.codigo.code.test.repo.CourseRepository;
import com.codigo.code.test.service.CourseService;
import com.codigo.code.test.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourserServiceImpl implements CourseService {

    private final CourseRepository courseRepo;
    private final CountryRepository countryRepo;
    private final RedisService redisService;

    @Value("${redis.expiration.time}")
    private int redisExpirationTime;

    @Transactional(readOnly = true)
    @Override
    public Response getAllCourses() {
        try {
            List<Course> courses = courseRepo.findAll();
            log.info("Fetched {} courses", courses.size());

            List<CourseDto> courseDtos = courses.stream().map(CourseDto::new).toList();
            courseDtos.forEach(courseDto -> {
                log.info("Course ID: {}, Name: {}", courseDto.getId(), courseDto.getName());
            });

            return ResponseBuilder.newBuilder()
                    .withData(courseDtos)
                    .withKey("courses")
                    .build();
        } catch (Exception e) {
            log.error("Error fetching courses: {}", e.getMessage());
            throw new ApplicationException("Error fetching courses", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Response getCourseById(Long id) {
        Course course = courseRepo.findById(id).orElseThrow(() -> new ApplicationException("Course not found", HttpStatus.NOT_FOUND));
        log.info("Fetched course {}", course);

        return ResponseBuilder.newBuilder()
                .withData(new CourseDto(course))
                .build();
    }

    @Transactional
    @Override
    public Response createCourse(CourseRequest courseRequest) {
        try {
            Country country = (Country) countryRepo.findByCountryCode(courseRequest.countryCode())
                    .orElseThrow(() -> new ApplicationException("Country not found", HttpStatus.NOT_FOUND));
            Course course = new Course(courseRequest, country);
            log.info("Course {}", course);

            courseRepo.save(course);

            saveAvailableSlotInRedis(course);

            return ResponseBuilder.newBuilder()
                    .withData(new CourseDto(course))
                    .withHttpStatus(HttpStatus.CREATED)
                    .build();
        } catch (Exception e) {
            log.error("Error creating course: {}", e.getMessage());
            throw new ApplicationException("Error creating course", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response getCoursesByCountryCode(String countryCode) {
        try {
            List<Course> courses = courseRepo.findByCountryCountryCode(countryCode);
            log.info("Fetched {} courses for country code {}", courses.size(), countryCode);

            List<CourseDto> courseDtos = courses.stream().map(CourseDto::new).toList();
            return ResponseBuilder.newBuilder()
                    .withData(courseDtos)
                    .withKey("courses")
                    .build();
        } catch (Exception e) {
            log.error("Error fetching courses by country code: {}", e.getMessage());
            throw new ApplicationException("Error fetching courses by country code", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveAvailableSlotInRedis(Course course) {
        String key = "course:" + course.getId() + ":available_slot";
        redisService.set(key, String.valueOf(course.getSlot()), redisExpirationTime);
        log.info("Saved available slot for course {} in Redis", course.getId());
    }

    @Override
    public Response updateCourse(Long id, CourseRequest courseRequest) {
        return null;
    }

    @Override
    public Response deleteCourse(Long id) {
        return null;
    }
}
