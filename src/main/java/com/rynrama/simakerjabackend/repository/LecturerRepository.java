package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.LecturerModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LecturerRepository extends JpaRepository<LecturerModel, UUID> {
}
