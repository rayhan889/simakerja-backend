package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.MoaIADocumentRequest;
import com.rynrama.simakerjabackend.exception.UserNotFoundException;
import com.rynrama.simakerjabackend.model.MoaIADocumentModel;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.model.SubmissionType;
import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;
import com.rynrama.simakerjabackend.repository.SubmissionRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import com.rynrama.simakerjabackend.util.NumericRandomGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DocumentSubmissionService {

    private final SubmissionRepository submissionRepository;
    private final MoAIADocumentRepository moAIADocumentRepository;
    private final UserRepository userRepository;

    public DocumentSubmissionService(
            SubmissionRepository submissionRepository,
            MoAIADocumentRepository moAIADocumentRepository,
            UserRepository userRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.moAIADocumentRepository = moAIADocumentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveDocument(
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
        moaIADocument.setStudentSnapshot(moaIADocumentRequest.getStudentSnapshot());

        moAIADocumentRepository.save(moaIADocument);
    }

    public void saveCooperationRequestDocument() {
    }

    public void saveMouRequestDocument() {
    }

    public void saveVisitRequestDocument() {
    }

}
