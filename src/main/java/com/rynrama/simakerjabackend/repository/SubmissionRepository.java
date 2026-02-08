package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<SubmissionModel, UUID> {

    @Query("""
        select new com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO(
            s.id,
            s.submissionType,
            s.status,
            s.notes,
            s.faculty,
            s.submissionDate
        )
        from SubmissionModel s
    """)
    Page<DocumentSubmissionDTO> findAllDocuments(Pageable pageable);
}
