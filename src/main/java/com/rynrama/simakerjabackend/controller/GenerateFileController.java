package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.config.security.CustomUserPrincipal;
import com.rynrama.simakerjabackend.model.MoAIAPDFViewModel;
import com.rynrama.simakerjabackend.service.DocumentSubmissionService;
import com.rynrama.simakerjabackend.service.GenerateFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/generate-file")
public class GenerateFileController {

    private final GenerateFileService  generateFileService;
    private final DocumentSubmissionService documentSubmissionService;

    public GenerateFileController(
            GenerateFileService generateFileService,
            DocumentSubmissionService documentSubmissionService
    ) {
        this.generateFileService = generateFileService;
        this.documentSubmissionService = documentSubmissionService;
    }

    @GetMapping(value = "/moa-ia/{submission_id}", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'STAFF', 'LECTURER')")
    public ResponseEntity<byte[]> generateFile(
        @PathVariable("submission_id") String submissionId
    ) throws Exception {

        MoAIAPDFViewModel moaIaData = documentSubmissionService.buildMoAIAData(submissionId);
        System.out.println("Partner name from controller: " + moaIaData.getPartnerName());

        byte[] pdf = generateFileService.generatePdf(moaIaData);

        String fileName = "moa_ia_" + UUID.randomUUID() + ".pdf";
        System.out.println("PDF file generated: " + fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; " + "filename=" +  fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }
}
