package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.StaffVerifySubmissionRequest;
import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.model.SubmissionModel;
import com.rynrama.simakerjabackend.model.SubmissionStatus;
import com.rynrama.simakerjabackend.repository.StaffRepository;
import com.rynrama.simakerjabackend.repository.SubmissionRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {

    private final StaffRepository staffRepo;
    private final SubmissionRepository  submissionRepo;

    public StaffService(StaffRepository staffRepo,  SubmissionRepository submissionRepo) {
        this.staffRepo = staffRepo;
        this.submissionRepo = submissionRepo;
    }

    @Transactional
    public SubmissionModel verifySubmissionByStaff(
            StaffVerifySubmissionRequest request,
            String submissionId
    ) throws BadRequestException {
        SubmissionModel submission = submissionRepo
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Submission with id: " + submissionId + " not found"
                        )
                );

        if (request.getSubmissionStatus() != SubmissionStatus.verified_staff) {
            throw new BadRequestException("submission status must be verified_staff");
        }

        if (!isVerifiable(submission)) {
            throw new BadRequestException("submission cannot be verified by staff anymore");
        }

        submission.setStatus(request.getSubmissionStatus());

        return submission;
    }

    private static Boolean isVerifiable(SubmissionModel submission) {
        if (submission == null) return false;

        if (submission.getStaff() != null || submission.getStaffVerifiedAt() != null) return false;

        return !submission.getStatus().equals(SubmissionStatus.verified_adhoc) && !submission.getStatus().equals(SubmissionStatus.verified_staff);
    }
}
