package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.StudentInfo;
import com.rynrama.simakerjabackend.dto.StudentUpdateRequest;
import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.model.StudentModel;
import com.rynrama.simakerjabackend.repository.StudentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepo;

    public StudentService(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    @Transactional
    public StudentModel updateStudent(StudentUpdateRequest request, UUID userId) throws Exception {

        StudentModel student = studentRepo
                .findByUserId(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Student with id: " + userId + " not found.")
                );

        if (!Objects.equals(student.getUser().getStatus(), "active")) {
            throw new BadRequestException("Student with id: " + userId + " is not active.");
        }

        student.setNim(request.getNim());
        student.setStudyProgram(request.getStudyProgram());

        return student;
    }

    public Optional<StudentModel> findStudentByUserId(UUID userId) {
        return studentRepo.findByUserId(userId);
    }

    public List<StudentInfo> findAllRegisteredStudents(String excludeNim) {
        return studentRepo.findAllRegisteredStudents(excludeNim);
    }
}
