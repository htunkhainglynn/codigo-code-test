package com.codigo.code.test.service;

import com.codigo.code.test.dto.request.CourseRequest;
import com.codigo.code.test.dto.response.Response;

public interface CourseService {
    // Define the methods that will be implemented in the service class
    Response getAllCourses();
    Response getCourseById(Long id);
    Response createCourse(CourseRequest courseRequest);
    Response updateCourse(Long id, CourseRequest courseRequest);
    Response deleteCourse(Long id);

    Response getCoursesByCountryCode(String countryCode);
}
