package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.UpdateSubmissionRequest;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.service.LecturerService;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/adhocs")
@PreAuthorize("hasRole('LECTURER')")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @PutMapping("/process-moa-ia/{submission_id}")
    public ResponseEntity<GlobalAPIResponse<SubmissionModel>> updateSubmissionStatusToVerifiedByAdhoc(
            @PathVariable("submission_id") String submissionId,
            @Valid @RequestBody UpdateSubmissionRequest request
            ) throws Exception {

        String userId = (String) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getPrincipal();

        assert userId != null;
        var submission = lecturerService.processSubmissionByAdhoc(submissionId, request, UUID.fromString(userId));

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(GlobalAPIResponse.success(submission));
    }
}
