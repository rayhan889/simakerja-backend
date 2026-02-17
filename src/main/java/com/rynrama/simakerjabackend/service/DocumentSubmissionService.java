package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.*;
import com.rynrama.simakerjabackend.exception.DuplicateResourceException;
import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.exception.UserNotFoundException;
import com.rynrama.simakerjabackend.model.*;
import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;
import com.rynrama.simakerjabackend.repository.SubmissionRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import com.rynrama.simakerjabackend.util.IndonesianNumberConverter;
import com.rynrama.simakerjabackend.util.NumericRandomGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DocumentSubmissionService {

    private final SubmissionRepository submissionRepository;
    private final MoAIADocumentRepository moAIADocumentRepository;
    private final UserRepository userRepository;
    private final MinioService minioService;

    public DocumentSubmissionService(
            SubmissionRepository submissionRepository,
            MoAIADocumentRepository moAIADocumentRepository,
            UserRepository userRepository,
            MinioService minioService
    ) {
        this.submissionRepository = submissionRepository;
        this.moAIADocumentRepository = moAIADocumentRepository;
        this.userRepository = userRepository;
        this.minioService = minioService;
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
        submission.setFacultyAddress(submission.getFacultyAddress());

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
        moaIADocument.setPartnerAddress(moaIADocumentRequest.getPartnerAddress());
        moaIADocument.setPartnerLogoKey(moaIADocumentRequest.getPartnerLogoKey());

        moAIADocumentRepository.save(moaIADocument);
    }

    public void saveCooperationRequestDocument() {
    }

    public void saveMouRequestDocument() {
    }

    public void saveVisitRequestDocument() {
    }

    public MoAIAPDFViewModel buildMoAIAData(UUID submissionId) throws Exception {
        MoAIAPDFViewModel data = new MoAIAPDFViewModel();

        MoaIADocumentModel moaIAData = moAIADocumentRepository.
                findById(submissionId).
                orElseThrow(() ->
                    new ResourceNotFoundException(
                            "MoA and IA document not found with submission id" + submissionId
                    )
                );

        ZoneId zone = ZoneId.of("Asia/Jakarta");

        data.setFacultyName(moaIAData.getSubmission().getFaculty());
        data.setFacultyRepresentativeName(moaIAData.getFacultyRepresentativeName());
        data.setFacultyAddress(moaIAData.getSubmission().getFacultyAddress());
        data.setPartnerName(moaIAData.getPartnerName());

        String partnerLogoPreviewUrl = minioService.getPresignedUrl(moaIAData.getPartnerLogoKey());

        data.setPartnerLogoUrl(partnerLogoPreviewUrl);
        data.setPartnerNumber(moaIAData.getPartnerNumber());
        data.setPartnerRepresentativeName(moaIAData.getPartnerRepresentativeName());
        data.setPartnerRepresentativePosition(moaIAData.getPartnerRepresentativePosition());
        data.setActivityType(moaIAData.getActivityType());

        Instant submissionDate =  moaIAData.getSubmission().getSubmissionDate();
        ZonedDateTime zdt = submissionDate.atZone(zone);

        DateTimeFormatter dayFormatter =
                DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("id-ID"));

        DateTimeFormatter monthFormatter =
                DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("id-ID"));

        String day = zdt.format(dayFormatter);
        String monthName = zdt.format(monthFormatter);
        String date = String.valueOf(zdt.getDayOfMonth());
        int year = zdt.getYear();
        String yearInLongText = IndonesianNumberConverter.toWords(year);
        String ddMMyyyFormatDate =  zdt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        data.setDay(day);
        data.setDate(date);
        data.setMonth(monthName);
        data.setYearInLongText(yearInLongText);
        data.setDdMMyyyyFormatDate(ddMMyyyFormatDate);

        data.setStudentSnapshots(moaIAData.getStudentSnapshots());

        return data;
    }

    public List<PartnerProfileDTO> findAllExistingPartners(String search) {
        return moAIADocumentRepository.findAllExistingPartners(search);
    }

}
