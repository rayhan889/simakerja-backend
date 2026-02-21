package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.dto.StudentSubmissionPaginationDTO;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import io.lettuce.core.dynamic.annotation.Param;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<SubmissionModel, UUID> {

    @Query("""
        select new com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO(
            s.id,
            s.user,
            s.submissionCode,
            s.submissionType,
            s.status,
            s.notes,
            s.faculty,
            s.submissionDate
        )
        from SubmissionModel s where ( :status is null or :status = '' or s.status = :status )
            and ( :subsType is null or :subsType = '' or s.submissionType = :subsType)
    """)
    Page<DocumentSubmissionDTO> findAllSubmissions(
            Pageable pageable,
            @Param("status") String status,
            @Param("subsType") String subsType
    );

    @Query("""
    select new com.rynrama.simakerjabackend.dto.StudentSubmissionPaginationDTO(
        s.id,
        m.partnerName,
        m.partnerNumber,
        s.status,
        m.activityType,
        s.submissionDate,
        s.notes,
        m.documentType
    )
    from SubmissionModel s
        left join MoaIADocumentModel m
            on s.id = m.submission.id
                where s.user.id = :userId
                    and ( :status is null or :status = '' or s.status = :status )
                    and ( :search is null or :search = ''
                            or lower(m.partnerName) like lower(concat('%', CAST(:search AS string), '%'))
                            or lower(m.partnerNumber) like lower(concat('%', CAST(:search AS string), '%'))
                        )
""")
    Page<StudentSubmissionPaginationDTO> findSubmissionsByUserIdAndMoAIAType(
            Pageable pageable,
            @Param("userId") UUID userId,
            @Param("status") String status,
            @Param("search") String search
    );

    @Query("""
    select s from SubmissionModel s where s.id = :id
""")
    Optional<SubmissionModel> findById(String id);

    @Query("""
        select s from SubmissionModel s
            join fetch s.user
                where s.id = :submissionId
    """)
    Optional<SubmissionModel> findByIdWithUser(@Param("submissionId") String submissionId);
}
