package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.config.security.CustomUserPrincipal;
import com.rynrama.simakerjabackend.dto.*;
import com.rynrama.simakerjabackend.mapper.DocumentSubmissionMapper;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.service.DocumentSubmissionService;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import okhttp3.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
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
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(value = "search", required = false)  String search
    ) {
        UUID userId = principal.getUser().getId();

        Page<MoAIADocumentDTO> moaIa = documentService.findPaginatedMoAIA(
                pageable,
                userId,
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
            @PathVariable("user_id") UUID userId
    ) {
        Page<StudentSubmissionPaginationDTO> moaIa = documentService.findSubmissionsByUserIdAndMoAIAType(
                pageable,
                userId,
                status,
                search
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(moaIa));
    }

    @GetMapping("/moa-ia/by-submission/{submission_id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF')")
    public Optional<MoAIADocumentDTO>  findMoAIADetailsBySubmissionId(
            @PathVariable("submission_id") UUID submissionId
    ) {
        return documentService.findMoAIADetailsBySubmissionId(submissionId);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalAPIResponse<SubmissionModel>> submitDocument(
        @Valid @RequestBody DocumentSubmissionRequest request,
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        SubmissionModel submission = documentMapper.toModel(request);

        String userEmail = principal.getEmail();
        SubmissionModel createdSubmission = documentService.saveDocument(
                submission,
                userEmail,
                request.getMoaIa()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GlobalAPIResponse.success(createdSubmission, "Submission successfully created"));
    }

    @GetMapping("/partners")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF')")
    public ResponseEntity<GlobalAPIResponse<List<PartnerProfileDTO>>> getAllExistingPartners(
            @RequestParam(value = "search", required = false)  String search
    ) {
        List<PartnerProfileDTO> partnerNames = documentService.findAllExistingPartners(search);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(partnerNames));
    }
}
