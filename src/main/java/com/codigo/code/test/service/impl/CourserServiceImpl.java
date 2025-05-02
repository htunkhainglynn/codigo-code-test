package com.codigo.code.test.service.impl;

import com.codigo.code.test.dto.request.CourseRequest;
import com.codigo.code.test.dto.response.CourseDto;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.entity.Course;
import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.repo.CourseRepository;
import com.codigo.code.test.service.CourseService;
import com.codigo.code.test.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourserServiceImpl implements CourseService {

    private final CourseRepository courseRepo;

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
            Course course = new Course(courseRequest);
            log.info("Course {}", course);

            courseRepo.save(course);

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
    public Response updateCourse(Long id, CourseRequest courseRequest) {
        return null;
    }

    @Override
    public Response deleteCourse(Long id) {
        return null;
    }
}
