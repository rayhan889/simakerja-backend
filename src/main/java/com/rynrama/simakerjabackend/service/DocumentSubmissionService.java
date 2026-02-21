package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.*;
import com.rynrama.simakerjabackend.exception.DuplicateResourceException;
import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.exception.UserNotFoundException;
import com.rynrama.simakerjabackend.mapper.DocumentSubmissionMapper;
import com.rynrama.simakerjabackend.model.*;
import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;
import com.rynrama.simakerjabackend.repository.StudentRepository;
import com.rynrama.simakerjabackend.repository.SubmissionRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import com.rynrama.simakerjabackend.util.IndonesianNumberConverter;
import com.rynrama.simakerjabackend.util.NumericRandomGenerator;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DocumentSubmissionService {

    private final SubmissionRepository submissionRepository;
    private final MoAIADocumentRepository moAIADocumentRepository;
    private final UserRepository userRepository;
    private final MinioService minioService;
    private final DocumentSubmissionMapper documentSubmissionMapper;

    public DocumentSubmissionService(
            SubmissionRepository submissionRepository,
            MoAIADocumentRepository moAIADocumentRepository,
            UserRepository userRepository,
            MinioService minioService,
            DocumentSubmissionMapper documentSubmissionMapper
    ) {
        this.submissionRepository = submissionRepository;
        this.moAIADocumentRepository = moAIADocumentRepository;
        this.userRepository = userRepository;
        this.minioService = minioService;
        this.documentSubmissionMapper = documentSubmissionMapper;
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
    public DocumentSubmissionDTO findSubmissionDetailsBySubmissionId(String submissionId) {
        SubmissionModel submission = submissionRepository
                .findByIdWithUser(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission with id: " + submissionId + " not found"
                ));

        return switch (submission.getSubmissionType()) {
            case moa_ia -> {
                MoaIADocumentModel moaIaDoc = moAIADocumentRepository
                        .findBySubmissionId(submissionId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "MoA/IA document not found for submission id: " + submissionId
                        ));

                yield documentSubmissionMapper.toDetailDTO(submission, moaIaDoc);
            }

            case cooperation_request ->
                // TODO: Implement when CooperationRequestDTO and repository exist
                    throw new UnsupportedOperationException(
                            "Cooperation request details not yet implemented"
                    );

            case mou_request ->
                // TODO: Implement when MouRequestDTO and repository exist
                    throw new UnsupportedOperationException(
                            "MoU request details not yet implemented"
                    );

            case visit_request ->
                // TODO: Implement when VisitRequestDTO and repository exist
                    throw new UnsupportedOperationException(
                            "Visit request details not yet implemented"
                    );
        };
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
    ) throws Exception {
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
    ) throws Exception {
        var moaIADocument = new MoaIADocumentModel();

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

//    teknik_informatika > Teknik Informatika
    public static String formatString(String input) {
        if (input == null || input.isBlank()) return input;

        String[] words = input.replace("_", " ").toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

//    for student snapshots: [dono, joko] > 1. Dono 2. Joko
    public static String toNumberedHtmlList(List<StudentInfo> items) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            result.append(i + 1).append(". ").append(items.get(i).getFullName());
            if (i < items.size() - 1) result.append("<br/>");
        }
        return result.toString();
    }

    public MoAIAPDFViewModel buildMoAIAData(String submissionId) throws Exception {
        MoAIAPDFViewModel data = new MoAIAPDFViewModel();

        MoaIADocumentModel moaIAData = moAIADocumentRepository.
                findBySubmissionId(submissionId).
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
        data.setPartnerAddress(moaIAData.getPartnerAddress());

        String formattedActivityType = "";
        switch (moaIAData.getActivityType()) {
            case DocumentActivityType.internship -> formattedActivityType = "Magang";
            case DocumentActivityType.kkn ->  formattedActivityType = "KKN";
            case DocumentActivityType.plp ->   formattedActivityType = "PLP";
        }
        data.setActivityType(formattedActivityType);

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

        List<StudentSnapshot> studentSnapshots = moaIAData.getStudentSnapshots();
        List<StudentSnapshotDisplayDTO> displayStudentSnapshotsDTO = new ArrayList<>();
        for (StudentSnapshot snapshot:  studentSnapshots) {

            StudentSnapshotDisplayDTO snapShotDTO = new StudentSnapshotDisplayDTO(
                    formatString(snapshot.getStudyProgram()),
                    snapshot.getUnit(),
                    toNumberedHtmlList(snapshot.getStudents()),
                    snapshot.getTotal()
            );
            displayStudentSnapshotsDTO.add(snapShotDTO);
        }

        data.setUnesaLogoUrl(minioService.getPresignedUrl("unesa_logo.png"));

        data.setStudentSnapshots(displayStudentSnapshotsDTO);

        return data;
    }

    public List<PartnerProfileDTO> findAllVerifiedExistingPartners(String search) {
        return moAIADocumentRepository.findAllVerifiedExistingPartners(search);
    }

    @Transactional
    public SubmissionModel updateDocument(
            DocumentUpdateRequest request,
            String submissionId
    ) throws Exception {
        SubmissionModel submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Submission with id: " + submissionId + " not found"
                        )
                );

        submission.setNotes(request.getNotes());

        if (
                submission.getStatus() == SubmissionStatus.verified_adhoc ||
                        submission.getStatus() == SubmissionStatus.completed ||
                            submission.getStatus() == SubmissionStatus.verified_staff
        ) {
            throw new BadRequestException("moaIa already verified/completed by staff/adhoc");
        }

        switch (submission.getSubmissionType()) {
            case SubmissionType.moa_ia -> {
                if (request.getMoaIa() == null) {
                    throw new BadRequestException("moaIa is required");
                }
                updateMoaIa(request.getMoaIa(), submission.getId());
            }
        }

        return submission;
    }

    public void updateMoaIa(
            MoAIADocumentUpdateRequest request,
            String submissionId
    ) {
        MoaIADocumentModel moaIa = moAIADocumentRepository
                .findBySubmissionId(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Moa IA with submission id: " + submissionId + " not found"
                        )
                );

        moaIa.setPartnerName(request.getPartnerName());
        moaIa.setPartnerNumber(request.getPartnerNumber());
        moaIa.setFacultyRepresentativeName(request.getFacultyRepresentativeName());
        moaIa.setPartnerRepresentativeName(request.getPartnerRepresentativeName());
        moaIa.setPartnerRepresentativePosition(request.getPartnerRepresentativePosition());
        moaIa.setActivityType(request.getActivityType());
        moaIa.setStudentSnapshots(request.getStudentSnapshots());
        moaIa.setPartnerAddress(request.getPartnerAddress());
        moaIa.setPartnerLogoKey(request.getPartnerLogoKey());
    }

}
