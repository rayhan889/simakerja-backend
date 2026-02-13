package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.dto.MoAIADocumentDTO;
import com.rynrama.simakerjabackend.dto.MoaIADocumentRequest;
import com.rynrama.simakerjabackend.dto.StudentSubmissionPaginationDTO;
import com.rynrama.simakerjabackend.exception.UserNotFoundException;
import com.rynrama.simakerjabackend.mapper.DocumentSubmissionMapper;
import com.rynrama.simakerjabackend.model.*;
import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;
import com.rynrama.simakerjabackend.repository.SubmissionRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import com.rynrama.simakerjabackend.util.NumericRandomGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentSubmissionService {

    private final SubmissionRepository submissionRepository;
    private final MoAIADocumentRepository moAIADocumentRepository;
    private final UserRepository userRepository;
    private final DocumentSubmissionMapper documentMapper;

    public DocumentSubmissionService(
            SubmissionRepository submissionRepository,
            MoAIADocumentRepository moAIADocumentRepository,
            UserRepository userRepository,
            DocumentSubmissionMapper documentMapper
    ) {
        this.submissionRepository = submissionRepository;
        this.moAIADocumentRepository = moAIADocumentRepository;
        this.userRepository = userRepository;
        this.documentMapper = documentMapper;
    }

    public Page<DocumentSubmissionDTO> findPaginatedSubmissions(
            Pageable pageable,
            String status,
            String subsType
    ) {
        return submissionRepository.findAllSubmissions(
                pageable,
                status,
                subsType
        );
    }

    public Optional<MoAIADocumentDTO> findMoAIADetailsBySubmissionId(UUID submissionId) {
        return moAIADocumentRepository.findAllMoAIABySubmissionId(submissionId);
    }

    public Page<MoAIADocumentDTO> findPaginatedMoAIA(
            Pageable pageable,
            UUID userId,
            String search
    ) {
        return moAIADocumentRepository.findAllMoAIADocumentsByUserEmail(pageable, userId, search);
    }

    public Page<StudentSubmissionPaginationDTO> findSubmissionsByUserIdAndMoAIAType(
        Pageable pageable,
        UUID userId,
        String status,
        String search
    ) {
        return submissionRepository.findSubmissionsByUserIdAndMoAIAType(
                pageable,
                userId,
                status,
                search
        );
    }

    @Transactional
    public SubmissionModel saveDocument(
            SubmissionModel submission,
            String userEmail,
            MoaIADocumentRequest moaIADocumentRequest
    ){
        NumericRandomGenerator numericRandomGenerator = new NumericRandomGenerator();

        submission.setSubmissionCode(numericRandomGenerator.generate(20));

        UserModel user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UserNotFoundException(
                                "user with email" + userEmail + " not found"
                        ));
        submission.setUser(user);
        submission.setSubmissionDate(Instant.now());
        submission.setCreatedAt(Instant.now());

        submissionRepository.save(submission);

        switch (submission.getSubmissionType()) {
            case SubmissionType.moa_ia -> saveMoaIADocument(submission, moaIADocumentRequest);
            case SubmissionType.cooperation_request -> saveCooperationRequestDocument();
            case SubmissionType.mou_request -> saveMouRequestDocument();
            case SubmissionType.visit_request -> saveVisitRequestDocument();
        }

        return submission;
    }

    public void saveMoaIADocument(
            SubmissionModel submission,
            MoaIADocumentRequest moaIADocumentRequest
    ) {
        MoaIADocumentModel moaIADocument = new MoaIADocumentModel();

        moaIADocument.setSubmission(submission);
        moaIADocument.setDocumentType(moaIADocumentRequest.getDocumentType());
        moaIADocument.setPartnerName(moaIADocumentRequest.getPartnerName());
        moaIADocument.setPartnerNumber(moaIADocumentRequest.getPartnerNumber());
        moaIADocument.setFacultyRepresentativeName(moaIADocumentRequest.getFacultyRepresentativeName());
        moaIADocument.setPartnerRepresentativeName(moaIADocumentRequest.getPartnerRepresentativeName());
        moaIADocument.setPartnerRepresentativePosition(moaIADocumentRequest.getPartnerRepresentativePosition());
        moaIADocument.setActivityType(moaIADocumentRequest.getActivityType());
        moaIADocument.setStudentSnapshots(moaIADocumentRequest.getStudentSnapshots());

        moAIADocumentRepository.save(moaIADocument);
    }

    public void saveCooperationRequestDocument() {
    }

    public void saveMouRequestDocument() {
    }

    public void saveVisitRequestDocument() {
    }

}
