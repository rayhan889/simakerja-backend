package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.dto.MoAIADocumentDTO;
import com.rynrama.simakerjabackend.model.MoaIADocumentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface MoAIADocumentRepository extends JpaRepository<MoaIADocumentModel, UUID> {

    @Query("""
        select new com.rynrama.simakerjabackend.dto.MoAIADocumentDTO(
            m.partnerName,
            m.partnerNumber,
            m.facultyRepresentativeName,
            m.partnerRepresentativeName,
            m.partnerRepresentativePosition,
            m.studentSnapshot,
            m.activityType,
            m.documentType
        )
        from MoaIADocumentModel m
    """)
    Optional<MoAIADocumentDTO> findAllMoAIABySubmissionId(UUID submissionId);
}
