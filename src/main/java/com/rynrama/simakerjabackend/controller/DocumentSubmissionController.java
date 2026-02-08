package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionRequest;
import com.rynrama.simakerjabackend.mapper.DocumentSubmissionMapper;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.service.DocumentSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/docs")
public class DocumentSubmissionController {

    private final DocumentSubmissionMapper documentMapper;
    private final DocumentSubmissionService documentService;

    public DocumentSubmissionController(DocumentSubmissionMapper documentMapper, DocumentSubmissionService documentService) {
        this.documentMapper = documentMapper;
        this.documentService = documentService;
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
