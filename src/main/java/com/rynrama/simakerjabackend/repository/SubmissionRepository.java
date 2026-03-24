package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.dto.*;
import com.rynrama.simakerjabackend.dto.AdhocSubmissionPaginationDTO;
import com.rynrama.simakerjabackend.model.DocumentActivityType;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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
            s.submissionDate,
            s.lecturerVerifiedAt,
            s.staffVerifiedAt
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
        s.user.id,
        s.user.fullName,
        st.nim,
        s.id,
        m.partnerName,
        m.partnerNumber,
        s.status,
        m.activityType,
        s.submissionDate,
        s.notes,
        m.documentType,
        s.period
    )
    from SubmissionModel s
        left join MoaIADocumentModel m on s.id = m.submission.id
            join StudentModel st on s.user.id = st.user.id
                where (
                s.user.id = :userId or exists (
                                        select 1 from StudentSnapshotModel ss
                                            join ss.students sss
                                        where ss.document.id = m.id and sss.nim = :nim
                                    )
                )
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
            @Param("search") String search,
            @Param("nim") String nim
    );

    @Query("""
    select s from SubmissionModel s where s.id = :id
""")
    Optional<SubmissionModel> findById(String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")}) // 5 second timeout
    @Query("select s from SubmissionModel s where s.id = :id")
    Optional<SubmissionModel> findByIdForUpdate(@Param("id") String id);

    @Query("""
        select s from SubmissionModel s
            join fetch s.user
                where s.id = :submissionId
    """)
    Optional<SubmissionModel> findByIdWithUser(@Param("submissionId") String submissionId);

    @Query("""
    select new com.rynrama.simakerjabackend.dto.StaffSubmissionPaginationDTO(
        s.period,
        m.partnerName,
        m.partnerNumber,
        m.activityType,
        count(s.id)
    ) from SubmissionModel s
        join MoaIADocumentModel m on s.id = m.submission.id
            where ( :search is null or :search = ''
                        or lower(m.partnerName) like lower(concat('%', CAST(:search AS string), '%'))
                        or lower(m.partnerNumber) like lower(concat('%', CAST(:search AS string), '%'))
                    )
            group by s.period, m.partnerName, m.partnerNumber, m.activityType
    """)
    Page<StaffSubmissionPaginationDTO> findStaffSubmissionsPagination(
            Pageable pageable,
            @Param("search") String search
    );

    @Query("""
    select new com.rynrama.simakerjabackend.dto.StaffSubmissionPaginationDetailDTO(
        s.id,
            s.submissionCode,
                s2.studyProgram,
                    u.fullName,
                        s2.nim,
                            s.status
        ) from SubmissionModel s
            join MoaIADocumentModel m on s.id = m.submission.id
                join UserModel u on s.user.id = u.id
                    left join StudentModel s2 on u.id = s2.user.id
                        where m.partnerName = :partnerName and function('to_char', s.period, 'YYYY-MM') = :period and m.activityType = :activityType
                            and ( :search is null or :search = ''
                                or lower(u.fullName) like lower(concat('%', CAST(:search AS string), '%'))
                                or lower(s2.nim) like lower(concat('%', CAST(:search AS string), '%'))
                            )
    """)
    Page<StaffSubmissionPaginationDetailDTO> findStaffSubmissionsPaginationDetail(
            Pageable pageable,
            String search,
            String partnerName,
            String period,
            DocumentActivityType activityType
    );

    @Query("""
    select new com.rynrama.simakerjabackend.dto.StaffSubmissionPaginationDetailHeaderDTO(
        m.partnerName,
            s.period,
                m.activityType
        ) from SubmissionModel s
            join MoaIADocumentModel m
                where m.partnerName = :partnerName and function('to_char', s.period, 'YYYY-MM') = :period and m.activityType = :activityType
    """)
    Optional<StaffSubmissionPaginationDetailHeaderDTO> findStaffSubmissionsPaginationHeaderDetail(
            String partnerName,
            String period,
            DocumentActivityType activityType
    );

    @Query("""
    select new com.rynrama.simakerjabackend.dto.AdhocSubmissionPaginationDTO(
        s.period,
        m.partnerName,
        m.partnerNumber,
        m.activityType,
        count(s.id)
    ) from SubmissionModel s
        join MoaIADocumentModel m on s.id = m.submission.id
            left join UserModel u on s.user.id = u.id
                join StudentModel stu on u.id = stu.user.id
                    where stu.studyProgram = :studyProgram
                        and ( :search is null or :search = ''
                                or lower(m.partnerName) like lower(concat('%', CAST(:search AS string), '%'))
                                or lower(m.partnerNumber) like lower(concat('%', CAST(:search AS string), '%'))
                            )
                    group by s.period, m.partnerName, m.partnerNumber, m.activityType
    """)
    Page<AdhocSubmissionPaginationDTO> findAdhocSubmissionsPagination(
            Pageable pageable,
            String studyProgram,
            @Param("search") String search
    );

    @Query("""
    select new com.rynrama.simakerjabackend.dto.AdhocSubmissionPaginationDetailDTO(
        s.id,
            s.submissionCode,
                u.fullName,
                    s2.nim,
                        s.status
        ) from SubmissionModel s
            join MoaIADocumentModel m on s.id = m.submission.id
                join UserModel u on s.user.id = u.id
                    left join StudentModel s2 on u.id = s2.user.id
                        where m.partnerName = :partnerName
                            and s2.studyProgram = :studyProgram 
                                and function('to_char', s.period, 'YYYY-MM') = :period 
                                    and m.activityType = :activityType
                                        and ( :search is null or :search = ''
                                            or lower(u.fullName) like lower(concat('%', CAST(:search AS string), '%'))
                                            or lower(s2.nim) like lower(concat('%', CAST(:search AS string), '%'))
                                        )
    """)
    Page<AdhocSubmissionPaginationDetailDTO> findAdhocSubmissionsPaginationDetail(
            Pageable pageable,
            @Param("search") String search,
            String partnerName,
            String period,
            DocumentActivityType activityType,
            String studyProgram
    );

    @Query("""
    select case when count(s) > 0 then true else false end
        from SubmissionModel s
            where s.period between :start and :end
                and s.user.id = :userId
    """)
    boolean isSubmissionOnHalfOfYearAlreadyExits(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("userId") UUID userId);
}
