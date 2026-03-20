package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.UpdateSubmissionRequest;
import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.model.LecturerModel;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.model.SubmissionStatus;
import com.rynrama.simakerjabackend.repository.LecturerRepository;
import com.rynrama.simakerjabackend.repository.SubmissionRepository;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class LecturerService {
    private final LecturerRepository lecturerRepo;
    private final SubmissionRepository  submissionRepo;

    public LecturerService(LecturerRepository lecturerRepo, SubmissionRepository submissionRepo) {
        this.lecturerRepo = lecturerRepo;
        this.submissionRepo = submissionRepo;
    }

    @Transactional
    public SubmissionModel processSubmissionByAdhoc(
            String submissionId,
            UpdateSubmissionRequest request,
            UUID lecturerUserId
    ) throws BadRequestException {
        log.info("Verifying submission={} by adhoc", submissionId);
        log.info("Request submission status={}", request.getSubmissionStatus());

        var submission = getSubmission(submissionId);

        ProcessableCheckResponse processableCheckResponse = (ProcessableCheckResponse) checkProcessible(submission, request);
        var errMsg = processableCheckResponse.errMessage();
        var isProcessible = processableCheckResponse.isProcessable();
        if (!errMsg.isEmpty() && !isProcessible) {
            log.warn("Submission={} is not processable. Reason={}", submissionId, errMsg);
            throw new BadRequestException("submission cannot be processed by adhoc/lecturer anymore. Reason " + errMsg);
        }

        LecturerModel lecturer = lecturerRepo
                .findByUserId(lecturerUserId)
                .orElseThrow(() -> {
                    log.warn("Cannot find lecturer with userId={}", lecturerUserId);
                    return new ResourceNotFoundException(
                            "Staff with userId: " + lecturerUserId + " not found"
                    );
                });

        Instant now = Instant.now();
        submission.setLecturer(lecturer);
        switch (request.getSubmissionStatus()) {
            case SubmissionStatus.verified_adhoc -> {
                submission.setStatus(SubmissionStatus.verified_adhoc);
                submission.setLecturerVerifiedAt(now);
                submission.setNotes("");

                log.info("Submission={} has been verified", submissionId);
            }
            case SubmissionStatus.rejected_adhoc -> {
                submission.setStatus(SubmissionStatus.rejected_adhoc);
                submission.setLecturerRejectedAt(now);
                submission.setNotes(request.getNotes());

                log.info("Submission={} has been rejected", submissionId);
            }
            case SubmissionStatus.completed -> {
                submission.setStatus(SubmissionStatus.completed);
                submission.setNotes("");

                log.info("Submission={} has been completed", submissionId);
            }
        }

        return submission;
    }

    private SubmissionModel getSubmission(String submissionId) throws BadRequestException {
        SubmissionModel submission;
        try {
            submission = submissionRepo
                    .findByIdForUpdate(submissionId)
                    .orElseThrow(() -> {
                        log.warn("Cannot find submission with id={}", submissionId);
                        return new ResourceNotFoundException(
                                "Submission with id: " + submissionId + " not found"
                        );
                    });
        } catch (PessimisticLockException e) {
            log.warn("Could not acquire lock on submission={}, another verification is in progress", submissionId);
            throw new BadRequestException("Submission is currently being verified by another adhoc/staff member. Please try again.");
        }

        return submission;
    }

    public record ProcessableCheckResponse(
            String errMessage,
            Boolean isProcessable
    ) {}

    private static ProcessableCheckResponse checkProcessible(SubmissionModel submission, UpdateSubmissionRequest request) {
        if (submission == null) return new ProcessableCheckResponse(
                "submission cannot be null or empty",
                false
        );

        if (
                request.getSubmissionStatus() != SubmissionStatus.verified_adhoc &&
                request.getSubmissionStatus() != SubmissionStatus.rejected_adhoc &&
                request.getSubmissionStatus() != SubmissionStatus.completed
        ) return new ProcessableCheckResponse(
                "submission status can only be either verified adhoc, rejected by adhoc or completed",
                false
        );

        if (request.getSubmissionStatus() == SubmissionStatus.verified_adhoc && submission.getStatus() == SubmissionStatus.rejected_staff)
            return new ProcessableCheckResponse(
                    "staff is rejected this document. Can't verified it yet",
                    false
            );

        boolean completed = submission.getStatus() == SubmissionStatus.completed && (submission.getStaffVerifiedAt() != null && submission.getLecturerVerifiedAt() != null);

        if (completed) return  new ProcessableCheckResponse(
                "submission already completed",
                false
        );

        return new  ProcessableCheckResponse(
                "",
                true
        );
    }
}
