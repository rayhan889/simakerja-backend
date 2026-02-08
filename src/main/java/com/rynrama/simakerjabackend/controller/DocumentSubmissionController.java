package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.dto.DocumentSubmissionRequest;
import com.rynrama.simakerjabackend.dto.MoAIADocumentDTO;
import com.rynrama.simakerjabackend.mapper.DocumentSubmissionMapper;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.service.DocumentSubmissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/docs")
public class DocumentSubmissionController {

    private final DocumentSubmissionMapper documentMapper;
    private final DocumentSubmissionService documentService;

    public DocumentSubmissionController(DocumentSubmissionMapper documentMapper, DocumentSubmissionService documentService) {
        this.documentMapper = documentMapper;
        this.documentService = documentService;
    }

    @GetMapping("")
    public Page<DocumentSubmissionDTO> getAllSubmissions(Pageable pageable) {
        return documentService.findPaginatedSubmissions(pageable);
    }

    @GetMapping("/{submission_id}")
    public Optional<MoAIADocumentDTO>  findMoAIABySubmissionId(
            @PathVariable("submission_id") UUID submissionId
    ) {
        return documentService.findMoAIABySubmissionId(submissionId);
    }

    @PostMapping("")
    public ResponseEntity<?> submitDocument(
        @Valid @RequestBody DocumentSubmissionRequest request,
        @AuthenticationPrincipal OidcUser oidcUser
    ) {
        SubmissionModel submission = documentMapper.toModel(request);

        String userEmail = oidcUser.getEmail();
        documentService.saveDocument(
                submission,
                userEmail,
                request.getMoaIa()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
