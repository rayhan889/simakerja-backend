package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.SubmissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<SubmissionModel, UUID> {
}
