package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentModel, UUID> {
}
