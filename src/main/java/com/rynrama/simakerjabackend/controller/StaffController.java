package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.StaffVerifySubmissionRequest;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.service.StaffService;
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
@RequestMapping("/staffs")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PutMapping("/verify-moa-ia/{submission_id}")
    @PreAuthorize("hasAnyRole('STAFF', 'SUPERADMIN')")
    public ResponseEntity<GlobalAPIResponse<SubmissionModel>> updateSubmissionStatusToVerifiedByStaff(
            @PathVariable("submission_id") String submissionId
    ) throws Exception {

        String userId = (String) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getPrincipal();

        assert userId != null;
        var submission = staffService.verifySubmissionByStaff(submissionId, UUID.fromString(userId));

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(GlobalAPIResponse.success(submission));
    }
}
