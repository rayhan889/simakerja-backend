package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.StudentInfo;
import com.rynrama.simakerjabackend.dto.StudentUpdateRequest;
import com.rynrama.simakerjabackend.model.StudentModel;
import com.rynrama.simakerjabackend.service.StudentService;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PutMapping("/{user_id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalAPIResponse<StudentModel>> updateStudent(
            @Valid @RequestBody StudentUpdateRequest request,
            @PathVariable("user_id") UUID userId
    ) throws Exception {

        StudentModel student = studentService.updateStudent(request, userId);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(GlobalAPIResponse.success(student));
    }

    @GetMapping("/registered")
    public ResponseEntity<GlobalAPIResponse<List<StudentInfo>>> getAllRegisteredStudents(
            @RequestParam(value = "exclude_nim", required = false) String excludeNim
    ) {

        var students = studentService.findAllRegisteredStudents(excludeNim);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(students));
    }
}
