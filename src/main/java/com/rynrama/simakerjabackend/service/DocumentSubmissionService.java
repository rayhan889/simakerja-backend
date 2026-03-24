package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.*;
import com.rynrama.simakerjabackend.exception.*;
import com.rynrama.simakerjabackend.mapper.DocumentSubmissionMapper;
import com.rynrama.simakerjabackend.mapper.MoAIADocumentMapper;
import com.rynrama.simakerjabackend.mapper.StudentSnapshotMapper;
import com.rynrama.simakerjabackend.model.*;
import com.rynrama.simakerjabackend.repository.*;
import com.rynrama.simakerjabackend.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Slf4j
public class DocumentSubmissionService {

    private final SubmissionRepository submissionRepository;
    private final MoAIADocumentRepository moAIADocumentRepository;
    private final UserRepository userRepository;
    private final MinioService minioService;
    private final DocumentSubmissionMapper documentSubmissionMapper;
    private final StudentRepository studentRepository;
    private final MoAIADocumentMapper moAIADocumentMapper;
    private final VerifiedPartnerRepository verifiedPartnerRepository;
    private final LecturerRepository lecturerRepository;
    private final PartnerDuplicateChecker partnerDuplicateChecker;

    public DocumentSubmissionService(
            SubmissionRepository submissionRepository,
            MoAIADocumentRepository moAIADocumentRepository,
            UserRepository userRepository,
            MinioService minioService,
            DocumentSubmissionMapper documentSubmissionMapper,
            StudentRepository studentRepository,
            MoAIADocumentMapper moAIADocumentMapper,
            VerifiedPartnerRepository verifiedPartnerRepository,
            LecturerRepository lecturerRepository,
            PartnerDuplicateChecker partnerDuplicateChecker
    ) {
        this.submissionRepository = submissionRepository;
        this.moAIADocumentRepository = moAIADocumentRepository;
        this.userRepository = userRepository;
        this.minioService = minioService;
        this.documentSubmissionMapper = documentSubmissionMapper;
        this.studentRepository = studentRepository;
        this.moAIADocumentMapper = moAIADocumentMapper;
        this.verifiedPartnerRepository = verifiedPartnerRepository;
        this.lecturerRepository = lecturerRepository;
        this.partnerDuplicateChecker = partnerDuplicateChecker;
    }

    public DocumentActivityType toValidDocumentActivityType(String value) {
        if (value == null || value.isBlank()) return null;

        return Arrays.stream(DocumentActivityType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    public SubmissionStatus  toValidSubmissionStatus(String value) {
        if (value == null || value.isBlank()) return null;

        return Arrays.stream(SubmissionStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
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
        Page<MoaIADocumentModel> moaIa = moAIADocumentRepository
                .findAllMoAIADocumentsByUserEmail(pageable, userId, search);

        if (moaIa.getTotalElements() == 0) {
            log.info("No moaIa documents found for userId: {}", userId);
        }

        return moaIa.map(moAIADocumentMapper::toDto);
    }

    public Page<StudentSubmissionPaginationDTO> findSubmissionsByUserIdAndMoAIAType(
        Pageable pageable,
        UUID userId,
        String status,
        String search,
        String nim
    ) {
        return submissionRepository.findSubmissionsByUserIdAndMoAIAType(
                pageable,
                userId,
                status,
                search,
                nim
        );
    }

    @Transactional
    public SubmissionModel saveDocument(
            SubmissionModel submission,
            UUID userId,
            MoaIADocumentRequest moaIADocumentRequest
    ) {
        log.info("Saving document. userId={}", userId);

        NumericRandomGenerator numericRandomGenerator = new NumericRandomGenerator();

        submission.setSubmissionCode(numericRandomGenerator.generate(20));

        UserModel user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(
                                "user with email" + userId + " not found"
                        ));

        if (!isStudentValid(user.getId())) {
            log.error("Student with email={} not found", userId);
            throw new StudentNotValidException(
                    "student not have a valid nim and study program yet. Set it first"
            );
        }

        submission.setUser(user);

        var submissionDate = Instant.now();
        submission.setSubmissionDate(submissionDate);

        LocalDate period = LocalDate.ofInstant(submissionDate, ZoneId.systemDefault());
        submission.setPeriod(period.withDayOfMonth(1));

//        Period check. Prevent submission on the same period of the year. 2 periods on a year
        int month = period.getMonthValue();
        LocalDate start = period.withMonth(month <= 6 ? 1 : 7);
        LocalDate end = period.withMonth(month <= 6 ? 6 : 12).with(TemporalAdjusters.lastDayOfMonth());
        if (submissionRepository.isSubmissionOnHalfOfYearAlreadyExits(start, end, userId)) {
            log.error("Submission already existed for this period of year. Start={}, End={}", start, end);
            throw new DuplicateResourceException(
                    "Pengajuan untuk periode ini telah dibuat. Pastikan untuk mengecek progresnya."
            );
        }

//        Duplication check. Only runs if it's in a new partner mode
//        1. exact match check
//        2. trigram similarity check
//        3. acronym check -> BCA == Bank Central Asia Tbk, but not Bank Cengdu Aegoon
        if (submission.getSubmissionType() == SubmissionType.moa_ia &&
                (moaIADocumentRequest != null && moaIADocumentRequest.getMode() == MoaIASubmissionMode.new_partner)) {
            DuplicateCheckResult check = partnerDuplicateChecker.checkPotentialDuplication(
                    moaIADocumentRequest.getPartnerName(),
                    moaIADocumentRequest.getPartnerNumber()
            );

            if (check.isBlocked()) {
                log.error("Duplicate partner found. Matched existing partner: {}, with match type: {}", check.getMatchedPartnerName(), check.getMatchType());
                throw new DuplicateResourceException(
                        "Mitra '" + check.getMatchedPartnerName() + " sudah terdaftar. " +
                                "Harap menggunakan mitra yang sudah ada atau ubah nama mitra."
                );
            } else if (check.isWarned()) {
                log.warn("There's possibility of partner duplication. User input partner name: {}, and existing partner name: {}", moaIADocumentRequest.getPartnerName(), check.getMatchedPartnerName());
            }
        }

        submission.setCreatedAt(Instant.now());
        submission.setFacultyAddress(submission.getFacultyAddress());

        submissionRepository.save(submission);

        log.debug("Submission saved. submission type={}", submission.getSubmissionType());
        switch (submission.getSubmissionType()) {
            case SubmissionType.moa_ia -> {
                assert moaIADocumentRequest != null;
                saveMoaIADocument(submission, moaIADocumentRequest);
            }
            case SubmissionType.cooperation_request -> saveCooperationRequestDocument();
            case SubmissionType.mou_request -> saveMouRequestDocument();
            case SubmissionType.visit_request -> saveVisitRequestDocument();
        }

        return submission;
    }

    private Boolean isStudentValid(UUID userId) {
        return studentRepository.isStudentValid(userId);
    }

    private void saveMoaIADocument(
            SubmissionModel submission,
            MoaIADocumentRequest moaIADocumentRequest
    ) {
        log.info("Saving Moa Ia document. submissionId={}. partnerName={}", submission.getId(), moaIADocumentRequest.getPartnerName());

        var moaIADocument = new MoaIADocumentModel();

        moaIADocument.setSubmission(submission);
        moaIADocument.setDocumentType(moaIADocumentRequest.getDocumentType());
        moaIADocument.setPartnerName(moaIADocumentRequest.getPartnerName());
        moaIADocument.setPartnerNumber(moaIADocumentRequest.getPartnerNumber());
        moaIADocument.setFacultyRepresentativeName(moaIADocumentRequest.getFacultyRepresentativeName());
        moaIADocument.setPartnerRepresentativeName(moaIADocumentRequest.getPartnerRepresentativeName());
        moaIADocument.setPartnerRepresentativePosition(moaIADocumentRequest.getPartnerRepresentativePosition());
        moaIADocument.setActivityType(moaIADocumentRequest.getActivityType());
        moaIADocument.setPartnerCooperationPeriod(moaIADocumentRequest.getPartnerCooperationPeriod());

        var snapshotEntities = StudentSnapshotMapper.toEntities(
                moaIADocumentRequest.getStudentSnapshots(),
                moaIADocument
        );
        log.info("Saving student snapshots. snapshot length={}", snapshotEntities.size());
        moaIADocument.setStudentSnapshots(snapshotEntities);
        moaIADocument.setPartnerAddress(moaIADocumentRequest.getPartnerAddress());
        moaIADocument.setPartnerLogoKey(moaIADocumentRequest.getPartnerLogoKey());

        String rawName = moaIADocumentRequest.getPartnerName();
        String normalizedName = PartnerNameNormalizer.normalize(rawName);

        moaIADocument.setPartnerNameNormalized(normalizedName);
        moaIADocument.setPartnerNameAcronym(PartnerNameNormalizer.acronym(normalizedName));

        moAIADocumentRepository.save(moaIADocument);
    }

    private void saveCooperationRequestDocument() {
    }

    private void saveMouRequestDocument() {
    }

    private void saveVisitRequestDocument() {
    }

    public MoAIAPDFViewModel buildMoAIAData(String submissionId) throws Exception {
        log.info("Building Moa IA document for PDF. submissionId={}", submissionId);

        MoAIAPDFViewModel data = new MoAIAPDFViewModel();

        MoaIADocumentModel moaIAData = moAIADocumentRepository
                .findBySubmissionId(submissionId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "MoA and IA document not found with submission id" + submissionId
                    )
                );

        ZoneId zone = ZoneId.of("Asia/Jakarta");

        data.setFacultyName(moaIAData.getSubmission().getFaculty());
        data.setFacultyRepresentativeName(moaIAData.getFacultyRepresentativeName());
        data.setFacultyAddress(moaIAData.getSubmission().getFacultyAddress());
        data.setPartnerName(moaIAData.getPartnerName());

        String partnerLogoPreviewUrl = minioService.getInternalPresignedUrl(moaIAData.getPartnerLogoKey());
        log.debug("PDF minio partnerLogoPreviewUrl={}", partnerLogoPreviewUrl);

        data.setPartnerLogoUrl(partnerLogoPreviewUrl);
        data.setPartnerNumber(moaIAData.getPartnerNumber());
        data.setPartnerRepresentativeName(moaIAData.getPartnerRepresentativeName());
        data.setPartnerRepresentativePosition(moaIAData.getPartnerRepresentativePosition());
        data.setPartnerAddress(moaIAData.getPartnerAddress());
        data.setPartnerCooperationperiod(moaIAData.getPartnerCooperationPeriod());
        data.setPartnerCooperationPeriodIntext(FormatNumber.toIndonesianWord(moaIAData.getPartnerCooperationPeriod()));

        String formattedActivityType = "";
        switch (moaIAData.getActivityType()) {
            case DocumentActivityType.internship -> formattedActivityType = "Magang";
            case DocumentActivityType.kkn ->  formattedActivityType = "KKN";
            case DocumentActivityType.plp ->   formattedActivityType = "PLP";
        }
        log.debug("PDF formattedActivityType={}", formattedActivityType);
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
        String yearInLongText = FormatNumber.toLongIndonesianWord(year);
        String ddMMyyyFormatDate =  zdt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        data.setDay(day);
        data.setDate(date);
        data.setMonth(monthName);
        data.setYearInLongText(yearInLongText);
        data.setDdMMyyyyFormatDate(ddMMyyyFormatDate);

        List<StudentSnapshotDisplayDTO> displayStudentSnapshotsDTO = new ArrayList<>();
        for (StudentSnapshotModel snapshot : moaIAData.getStudentSnapshots()) {

            List<StudentInfo> studentInfos = new ArrayList<>();
            if (snapshot.getStudents() != null) {
                for (StudentSnapshotStudentModel s : snapshot.getStudents()) {
                    studentInfos.add(new StudentInfo(
                            FormatString.formatFullname(s.getFullName()),
                            s.getEmail(),
                            s.getNim()
                    ));
                }
            }

            StudentSnapshotDisplayDTO snapShotDTO = new StudentSnapshotDisplayDTO(
                    FormatString.formatProgramStudy(snapshot.getStudyProgram()),
                    snapshot.getUnit(),
                    FormatNumber.toNumberedHtmlList(studentInfos),
                    snapshot.getTotal()
            );
            displayStudentSnapshotsDTO.add(snapShotDTO);
        }

        data.setUnesaLogoUrl(minioService.getInternalPresignedUrl("unesa_logo.png"));

        data.setStudentSnapshots(displayStudentSnapshotsDTO);

        return data;
    }

    public List<PartnerProfileDTO> findAllVerifiedExistingPartners(String search) {
        return verifiedPartnerRepository.findAllValidVerifiedPartners(search);
    }

    @Transactional
    public SubmissionModel updateDocument(
            DocumentUpdateRequest request,
            String submissionId,
            UUID userId
    ) throws Exception {
        log.info("Update submission. userId={}. submissionId={}", userId, submissionId);

        SubmissionModel submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Submission with id: " + submissionId + " not found"
                        )
                );

        if (!canEditMoaIa(userId, submission.getUser().getId())) {
            log.error("Edit submission can be done by student who submitted it");
            throw new InsufficientResourceException(
                    "edit moIa only can be done by student who submitted it"
            );
        }

        if (!isMoaIaEditable(submission)) {
            log.error("Submission is not editable in its current state. status={}", submission.getStatus());
            throw new BadRequestException(
                    "Submission is not editable. It may be waiting for staff review or already completed."
            );
        }

        boolean finalization = isFinalizationMode(submission);

        if (!finalization) {
            submission.setNotes(request.getNotes());
            submission.setStatus(SubmissionStatus.in_process);
            submission.setStaffVerifiedAt(null);
            submission.setStaffRejectedAt(null);
            submission.setLecturerVerifiedAt(null);
            submission.setLecturerRejectedAt(null);
        }

        switch (submission.getSubmissionType()) {
            case SubmissionType.moa_ia -> {
                if (request.getMoaIa() == null) {
                    throw new BadRequestException("moaIa is required");
                }
                updateMoaIa(request.getMoaIa(), submission, finalization);
            }
        }

        submission.setUpdatedAt(Instant.now());
        return submission;
    }

    private Boolean isMoaIaEditable(SubmissionModel submission) {
        if (submission == null) return false;

        boolean lecturerVerified = submission.getLecturer() != null
                && submission.getLecturerVerifiedAt() != null;
        boolean staffNotReviewedYet = submission.getStaff() == null
                && submission.getStaffVerifiedAt() == null
                && submission.getStaffRejectedAt() == null;
        if (lecturerVerified && staffNotReviewedYet) {
            return false;
        }

        boolean bothVerified = lecturerVerified
                && submission.getStaff() != null
                && submission.getStaffVerifiedAt() != null;
        if (bothVerified) {
            return true;
        }

        if (submission.getStatus() == SubmissionStatus.rejected_adhoc) {
            return true;
        }

        if (submission.getStatus() == SubmissionStatus.in_process) {
            return true;
        }
        return false;
    }

    private Boolean isFinalizationMode(SubmissionModel submission) {
        if (submission == null) return false;
        return submission.getLecturer() != null
                && submission.getLecturerVerifiedAt() != null
                && submission.getStaff() != null
                && submission.getStaffVerifiedAt() != null;
    }

    private Boolean canEditMoaIa(UUID userId, UUID applicantId) {
        log.info("canEditMoaIa: applicantId={}", applicantId);

        if (userId == null) return false;
        if (applicantId == null) return false;

        return userId.equals(applicantId);
    }

    public void updateMoaIa(
            MoAIADocumentUpdateRequest request,
            SubmissionModel submission,
            Boolean finalization
    ) throws BadRequestException {
        log.info("Update moa ia. submissionId={}. finalization={}", submission.getId(), finalization);
        MoaIADocumentModel moaIa = moAIADocumentRepository
                .findBySubmissionId(submission.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Moa IA with submission id: " + submission.getId() + " not found"
                        )
                );

        if (finalization) {
            if (request.getScannedDocumentKey() == null || request.getScannedDocumentKey().isEmpty()) {
                throw new BadRequestException("Scanned document is required for finalization");
            }
            moaIa.setScannedDocumentKey(request.getScannedDocumentKey());
            moaIa.setSendScannedAt(Instant.now());
            moaIa.setScannedDocumentOcrConfidentScore(request.getAverageConfidence());
        } else {
            moaIa.setPartnerName(request.getPartnerName());
            moaIa.setPartnerNumber(request.getPartnerNumber());
            moaIa.setFacultyRepresentativeName(request.getFacultyRepresentativeName());
            moaIa.setPartnerRepresentativeName(request.getPartnerRepresentativeName());
            moaIa.setPartnerRepresentativePosition(request.getPartnerRepresentativePosition());
            moaIa.setActivityType(request.getActivityType());
            moaIa.clearStudentSnapshots();
            var snapshotEntities = StudentSnapshotMapper.toEntities(
                    request.getStudentSnapshots(),
                    moaIa
            );
            moaIa.getStudentSnapshots().addAll(snapshotEntities);
            moaIa.setPartnerAddress(request.getPartnerAddress());
            moaIa.setPartnerLogoKey(request.getPartnerLogoKey());
            moaIa.setPartnerCooperationPeriod(request.getPartnerCooperationPeriod());
        }
    }

//    MoA IA Document Pagination for Staff
    public Page<StaffSubmissionPaginationDTO> findStaffSubmissionsPagination(
            Pageable pageable,
            String search
    ) {
        Pageable sort = staffSubmissionPaginationSort(pageable);
        return submissionRepository.findStaffSubmissionsPagination(sort, search);
    }

//    MoA IA Document Pagination detail for Staff
    public Page<StaffSubmissionPaginationDetailDTO> findStaffSubmissionsPaginationDetail(
            Pageable pageable,
            String search,
            String partnerName,
            String period,
            String activityType
    ) throws InvalidEnumException {
        var validActivityType = toValidDocumentActivityType(activityType);
        if (validActivityType == null) {
            throw new InvalidEnumException(
                    activityType + " is not a valid activity type"
            );
        }
        Pageable sort = staffSubmissionPaginationDetailSort(pageable);
        return submissionRepository.findStaffSubmissionsPaginationDetail(
                sort,
                search,
                partnerName,
                period,
                validActivityType
        );
    }

    public Optional<StaffSubmissionPaginationDetailHeaderDTO> findStaffSubmissionsPaginationHeaderDetail(
            String partnerName,
            String period,
            String activityType
    ) throws InvalidEnumException {
        var validActivityType = toValidDocumentActivityType(activityType);
        if (validActivityType == null) {
            throw new InvalidEnumException(
                    activityType + " is not a valid activity type"
            );
        }
        return submissionRepository.findStaffSubmissionsPaginationHeaderDetail(
                partnerName,
                period,
                validActivityType
        );
    }

    private Pageable staffSubmissionPaginationSort(Pageable pageable) {
        Sort requested = pageable.getSort();

        if (requested == null || requested.isUnsorted()) {
            Sort defaultSort = Sort.by(Sort.Order.desc("period"));
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        Sort mapped = Sort.unsorted();

        for (Sort.Order order : requested) {
            String key = order.getProperty();
            if (key == null) continue;

            String normalized = key.trim();

            if (normalized.equals("period")) {
                mapped = mapped.and(Sort.by(new Sort.Order(order.getDirection(), "period")));
                continue;
            }

            if (normalized.equals("partnerName") || normalized.equals("partner_name")) {
                mapped = mapped.and(JpaSort.unsafe(order.getDirection(), "m.partnerName"));
                continue;
            }

            if (normalized.equals("partnerNumber") || normalized.equals("partner_number")) {
                mapped = mapped.and(JpaSort.unsafe(order.getDirection(), "m.partnerNumber"));
                continue;
            }

        }

        if (mapped.isUnsorted()) {
            mapped = Sort.by(Sort.Order.desc("period"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), mapped);
    }

    private Pageable staffSubmissionPaginationDetailSort(Pageable pageable) {
        Sort requested = pageable.getSort();

        if (requested == null || requested.isUnsorted()) {
            Sort defaultSort = Sort.by(Sort.Order.desc("u.fullName"));
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        Sort mapped = Sort.unsorted();

        for (Sort.Order order : requested) {
            String key = order.getProperty();
            if (key == null) continue;

            String normalized = key.trim();

            if (normalized.equals("nim")) {
                mapped = mapped.and(JpaSort.unsafe(order.getDirection(), "s2.nim"));
                continue;
            }

        }

        if (mapped.isUnsorted()) {
            mapped = Sort.by(Sort.Order.desc("u.fullName"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), mapped);
    }

    public LecturerModel findLecturerByUserId(UUID userId) {
        return lecturerRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("No lecturer found for userId {}", userId);
                    return new ResourceNotFoundException("No lecturer found for userId " + userId);
                });
    }

    //    MoA IA Document Pagination for Adhoc
    public Page<AdhocSubmissionPaginationDTO> findAdhocSubmissionsPagination(
            Pageable pageable,
            String search,
            UUID userId
    ) {
        var adhoc = findLecturerByUserId(userId);
        var adhocStudyProgram = adhoc.getStudyProgram();

        Pageable sort = lecturerSubmissionPaginationSort(pageable);
        return submissionRepository.findAdhocSubmissionsPagination(sort, adhocStudyProgram, search);
    }

    //    MoA IA Document Pagination detail for Adhoc
    public Page<AdhocSubmissionPaginationDetailDTO> findAdhocSubmissionsPaginationDetail(
            Pageable pageable,
            String search,
            String partnerName,
            String period,
            String activityType,
            UUID userId
    ) throws InvalidEnumException {
        var validActivityType = toValidDocumentActivityType(activityType);
        if (validActivityType == null) {
            throw new InvalidEnumException(
                    activityType + " is not a valid activity type"
            );
        }

        var adhoc = findLecturerByUserId(userId);
        var adhocStudyProgram = adhoc.getStudyProgram();

        Pageable sort = lecturerSubmissionPaginationDetailSort(pageable);
        return submissionRepository.findAdhocSubmissionsPaginationDetail(
                sort,
                search,
                partnerName,
                period,
                validActivityType,
                adhocStudyProgram
        );
    }

    private Pageable lecturerSubmissionPaginationSort(Pageable pageable) {
        Sort requested = pageable.getSort();

        if (requested == null || requested.isUnsorted()) {
            Sort defaultSort = Sort.by(Sort.Order.desc("period"));
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        Sort mapped = Sort.unsorted();

        for (Sort.Order order : requested) {
            String key = order.getProperty();
            if (key == null) continue;

            String normalized = key.trim();

            if (normalized.equals("period")) {
                mapped = mapped.and(Sort.by(new Sort.Order(order.getDirection(), "period")));
                continue;
            }

            if (normalized.equals("partnerName") || normalized.equals("partner_name")) {
                mapped = mapped.and(JpaSort.unsafe(order.getDirection(), "m.partnerName"));
                continue;
            }

            if (normalized.equals("partnerNumber") || normalized.equals("partner_number")) {
                mapped = mapped.and(JpaSort.unsafe(order.getDirection(), "m.partnerNumber"));
                continue;
            }

        }

        if (mapped.isUnsorted()) {
            mapped = Sort.by(Sort.Order.desc("period"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), mapped);
    }

    private Pageable lecturerSubmissionPaginationDetailSort(Pageable pageable) {
        Sort requested = pageable.getSort();

        if (requested == null || requested.isUnsorted()) {
            Sort defaultSort = Sort.by(Sort.Order.desc("u.fullName"));
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        Sort mapped = Sort.unsorted();

        for (Sort.Order order : requested) {
            String key = order.getProperty();
            if (key == null) continue;

            String normalized = key.trim();

            if (normalized.equals("nim")) {
                mapped = mapped.and(JpaSort.unsafe(order.getDirection(), "s2.nim"));
                continue;
            }

        }

        if (mapped.isUnsorted()) {
            mapped = Sort.by(Sort.Order.desc("u.fullName"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), mapped);
    }

}
