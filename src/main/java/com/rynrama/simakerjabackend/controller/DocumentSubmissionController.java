package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.*;
import com.rynrama.simakerjabackend.exception.InvalidEnumException;
import com.rynrama.simakerjabackend.mapper.DocumentSubmissionMapper;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.service.DocumentSubmissionService;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
public class DocumentSubmissionController {

    private final DocumentSubmissionMapper documentMapper;
    private final DocumentSubmissionService documentService;

    public DocumentSubmissionController(DocumentSubmissionMapper documentMapper, DocumentSubmissionService documentService) {
        this.documentMapper = documentMapper;
        this.documentService = documentService;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF')")
    public ResponseEntity<GlobalAPIResponse<Page<DocumentSubmissionDTO>>> getAllSubmissions(
            Pageable pageable,
            @RequestParam("status") String status,
            @RequestParam("subsType") String subsType
    ) {
        Page<DocumentSubmissionDTO> submissions = documentService.findPaginatedSubmissions(
                pageable,
                status,
                subsType
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(submissions));
    }

    @GetMapping("/moa-ia")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF')")
    public ResponseEntity<GlobalAPIResponse<Page<MoAIADocumentDTO>>> getAllMoAIASubmissions(
            Pageable pageable,
            @RequestParam(value = "search", required = false)  String search
    ) {
        String userId = (String) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        assert userId != null;
        var moaIa = documentService.findPaginatedMoAIA(
                pageable,
                UUID.fromString(userId),
                search
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(moaIa));
    }

    @GetMapping("/moa-ia/{user_id}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<GlobalAPIResponse<Page<StudentSubmissionPaginationDTO>>> getSubmissionsByUserIdAndMoAIAType(
            Pageable pageable,
            @RequestParam(value = "search", required = false)  String search,
            @RequestParam(value = "status", required = false)  String status,
            @RequestParam(value = "nim", required = false) String nim,
            @PathVariable("user_id") UUID userId
    ) {
        Page<StudentSubmissionPaginationDTO> moaIa = documentService.findSubmissionsByUserIdAndMoAIAType(
                pageable,
                userId,
                status,
                search,
                nim
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(moaIa));
    }

    @GetMapping("/details/{submission_id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF')")
    public ResponseEntity<GlobalAPIResponse<DocumentSubmissionDTO>>  findSubmissionDetails(
            @PathVariable("submission_id") String submissionId
    ) {
        DocumentSubmissionDTO details = documentService.findSubmissionDetailsBySubmissionId(submissionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(details));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalAPIResponse<SubmissionModel>> submitDocument(
        @Valid @RequestBody DocumentSubmissionRequest request
    ) throws Exception {
        String userId = (String) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        SubmissionModel submission = documentMapper.toModel(request);

        assert userId != null;
        SubmissionModel createdSubmission = documentService.saveDocument(
                submission,
                UUID.fromString(userId),
                request.getMoaIa()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GlobalAPIResponse.success(createdSubmission, "Submission successfully created"));
    }

    @GetMapping("/partners")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF', 'SUPERADMIN')")
    public ResponseEntity<GlobalAPIResponse<List<PartnerProfileDTO>>> getAllVerifiedExistingPartners(
            @RequestParam(value = "search", required = false)  String search
    ) {
        List<PartnerProfileDTO> partnerNames = documentService.findAllVerifiedExistingPartners(search);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(partnerNames));
    }


    @PutMapping("/{submission_id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalAPIResponse<SubmissionModel>> updateDocument(
        @Valid @RequestBody DocumentUpdateRequest request,
        @PathVariable("submission_id") String submissionId
    ) throws Exception {
        String userId = (String) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        assert userId != null;
        SubmissionModel submission = documentService.updateDocument(request, submissionId, UUID.fromString(userId));

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(GlobalAPIResponse.success(submission));
    }

//    Staff
    @GetMapping("/moa-ia/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<GlobalAPIResponse<Page<StaffSubmissionPaginationDTO>>> getStaffSubmissionsPagination(
            Pageable pageable,
            @RequestParam(value = "search", required = false)  String search
    ) {

        var submissions = documentService.findStaffSubmissionsPagination(
                pageable,
                search
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(submissions));
    }

    @GetMapping("/moa-ia/detail/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<GlobalAPIResponse<Page<StaffSubmissionPaginationDetailDTO>>> getStaffSubmissionsPaginationDetail(
            Pageable pageable,
            @RequestParam(value = "search", required = false)  String search,
            @RequestParam(value = "partner_name") String partnerName,
            @RequestParam(value = "period") String period,
            @RequestParam(value = "activity_type") String activityType
    ) throws InvalidEnumException {

        var submissions = documentService.findStaffSubmissionsPaginationDetail(
                pageable,
                search,
                partnerName,
                period,
                activityType
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(submissions));
    }

    @GetMapping("/moa-ia/header/detail/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<GlobalAPIResponse<Optional<StaffSubmissionPaginationDetailHeaderDTO>>> getStaffSubmissionsPaginationHeaderDetail(
            @RequestParam(value = "partner_name") String partnerName,
            @RequestParam(value = "period") String period,
            @RequestParam(value = "activity_type") String activityType
    ) throws InvalidEnumException {

        var header = documentService.findStaffSubmissionsPaginationHeaderDetail(
                partnerName,
                period,
                activityType
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(header));
    }

//    Adhoc
    @GetMapping("/moa-ia/adhoc")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<GlobalAPIResponse<Page<AdhocSubmissionPaginationDTO>>> getAdhocSubmissionsPagination(
            Pageable pageable,
            @RequestParam(value = "search", required = false)  String search
    ) {
        String userId = (String) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        assert userId != null;
        var submissions = documentService.findAdhocSubmissionsPagination(
                pageable,
                search,
                UUID.fromString(userId)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(submissions));
    }

    @GetMapping("/moa-ia/detail/adhoc")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<GlobalAPIResponse<Page<AdhocSubmissionPaginationDetailDTO>>> getAdhocSubmissionsPaginationDetail(
            Pageable pageable,
            @RequestParam(value = "search", required = false)  String search,
            @RequestParam(value = "partner_name") String partnerName,
            @RequestParam(value = "period") String period,
            @RequestParam(value = "activity_type") String activityType
    ) throws InvalidEnumException {
        String userId = (String) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        assert userId != null;
        var submissions = documentService.findAdhocSubmissionsPaginationDetail(
                pageable,
                search,
                partnerName,
                period,
                activityType,
                UUID.fromString(userId)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(submissions));
    }

}
