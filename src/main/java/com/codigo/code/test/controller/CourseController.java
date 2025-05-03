package com.codigo.code.test.controller;

import com.codigo.code.test.dto.request.CourseRequest;
import com.codigo.code.test.dto.response.Response;
import com.codigo.code.test.service.impl.CourserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

     private final CourserServiceImpl courseService;

     @GetMapping("/all")
     public ResponseEntity<Response> getAllCourses() {
         return ok(courseService.getAllCourses());
     }

     @GetMapping("/{id}")
     public ResponseEntity<Response> getCourseById(@PathVariable Long id) {
         return ok(courseService.getCourseById(id));
     }

     @GetMapping("/country/{countryCode}")
     public ResponseEntity<Response> getCoursesByCountry(@PathVariable String countryCode) {
         return ok(courseService.getCoursesByCountryCode(countryCode));
     }


     @PostMapping("/create")
     public ResponseEntity<Response> createCourse(@RequestBody @Validated CourseRequest courseRequest) {
         return ok(courseService.createCourse(courseRequest));
     }
}
