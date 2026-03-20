package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.StaffVerifySubmissionRequest;
import com.rynrama.simakerjabackend.dto.UpdateSubmissionRequest;
import com.rynrama.simakerjabackend.exception.ResourceNotFoundException;
import com.rynrama.simakerjabackend.model.*;
import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;
import com.rynrama.simakerjabackend.repository.StaffRepository;
import com.rynrama.simakerjabackend.repository.SubmissionRepository;
import com.rynrama.simakerjabackend.repository.VerifiedPartnerRepository;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Slf4j
public class StaffService {

    private final StaffRepository staffRepo;
    private final SubmissionRepository  submissionRepo;
    private final VerifiedPartnerRepository verifiedPartnerRepo;
    private final MoAIADocumentRepository moAIADocumentRepo;

    public StaffService(
            StaffRepository staffRepo,
            SubmissionRepository submissionRepo,
            VerifiedPartnerRepository verifiedPartnerRepo,
            MoAIADocumentRepository moAIADocumentRepo
    ) {
        this.staffRepo = staffRepo;
        this.submissionRepo = submissionRepo;
        this.verifiedPartnerRepo = verifiedPartnerRepo;
        this.moAIADocumentRepo = moAIADocumentRepo;
    }

    @Transactional
    public SubmissionModel processSubmissionByStaff(
            String submissionId,
            UpdateSubmissionRequest request,
            UUID staffUserId
    ) throws BadRequestException {
        log.info("Verifying submission={} by staff", submissionId);

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
            throw new BadRequestException("Submission is currently being verified by another staff member. Please try again.");
        }

        ProcessableCheckResponse processableCheckResponse = (ProcessableCheckResponse) checkProcessible(submission, request);
        var errMsg = processableCheckResponse.errMessage();
        var isProcessible = processableCheckResponse.isProcessable();
        if (!errMsg.isEmpty() && !isProcessible) {
            log.warn("Submission={} is not verifiable. Reason={}", submissionId, errMsg);
            throw new IllegalArgumentException("submission cannot be verified by staff. Reason: " + errMsg);
        }

        StaffModel staff = staffRepo
                .findByUserId(staffUserId)
                .orElseThrow(() -> {
                    log.warn("Cannot find staff with userId={}", staffUserId);
                    return new ResourceNotFoundException(
                            "Staff with userId: " + staffUserId + " not found"
                    );
                });

        Instant now = Instant.now();
        switch (request.getSubmissionStatus()) {
            case SubmissionStatus.verified_staff -> {
                var moaIa = moAIADocumentRepo
                        .findBySubmissionId(submissionId)
                        .orElseThrow(() -> {
                            log.warn("Cannot found moaia document with id={}", submissionId);
                            return new ResourceNotFoundException("moaia document with id: " + submissionId + " not found");
                        });

                if (isPartnerAlreadyVerified(moaIa)) {
                    log.info("Partner={}, already verified. Skip creation of new verified partner", moaIa.getPartnerName());
                } else {
                    createVerifiedPartner(moaIa);
                }

                submission.setStatus(SubmissionStatus.verified_staff);
                if (submission.getStaff() == null) submission.setStaff(staff);
                submission.setStaffVerifiedAt(now);
                submission.setNotes("");

                log.info("Submission={} has been verified", submissionId);
            }
            case SubmissionStatus.rejected_staff -> {
                submission.setStatus(SubmissionStatus.rejected_staff);
                submission.setStaffRejectedAt(now);
                submission.setNotes(request.getNotes());
                submission.setLecturerVerifiedAt(null);
                if (submission.getStaff() == null) submission.setStaff(staff);

//                TODO: add another field for staff rejected at -> similar to lecturer
                log.info("Submission={} has been rejected", submissionId);
            }
        }

        return submission;
    }

    private void createVerifiedPartner(MoaIADocumentModel moaIa) {
        log.info("Copying verified partner data from moa ia={}", moaIa.getId());

        var verifiedPartner = new VerifiedPartnerModel();
        var now = Instant.now();

        verifiedPartner.setMoaIa(moaIa);
        verifiedPartner.setPartnerName(moaIa.getPartnerName());
        verifiedPartner.setPartnerAddress(moaIa.getPartnerAddress());
        verifiedPartner.setFacultyRepresentativeName(moaIa.getFacultyRepresentativeName());
        verifiedPartner.setPartnerRepresentativeName(moaIa.getPartnerRepresentativeName());
        verifiedPartner.setPartnerRepresentativePosition(moaIa.getPartnerRepresentativePosition());
        verifiedPartner.setActivityType(moaIa.getActivityType());
        verifiedPartner.setPartnerLogoKey(moaIa.getPartnerLogoKey());
        verifiedPartner.setPartnerCooperationPeriod(moaIa.getPartnerCooperationPeriod());
        verifiedPartner.setVerifiedAt(now);

        ZonedDateTime verifiedZdt = now.atZone(ZoneId.systemDefault());
        Instant verifiedUntil = verifiedZdt
                .plusYears(moaIa.getPartnerCooperationPeriod())
                .toInstant();
        verifiedPartner.setVerifiedUntil(verifiedUntil);

        log.info("Successfully creating new verified partner");
        verifiedPartnerRepo.save(verifiedPartner);
    }

    private boolean isPartnerAlreadyVerified(MoaIADocumentModel moaIa) {
        if (moaIa == null) return false;

        return verifiedPartnerRepo.isPartnerNameAndPartnerNumberExists(
                moaIa.getPartnerName(), moaIa.getPartnerNumber()
        );
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
                request.getSubmissionStatus() != SubmissionStatus.verified_staff &&
                request.getSubmissionStatus() != SubmissionStatus.rejected_staff
        ) return new ProcessableCheckResponse(
                "submission status can only be either verified staff or rejected by staff",
                false
        );

        if (submission.getStatus() == SubmissionStatus.completed) return new ProcessableCheckResponse(
                "submission already completed",
                false
        );

        if (submission.getLecturer() == null && submission.getLecturerVerifiedAt() == null) return new ProcessableCheckResponse(
                "submission must be verified by adhoc first",
                false
        );

        if (request.getSubmissionStatus() == SubmissionStatus.rejected_staff && (
            submission.getStaff() != null && submission.getStatus() == SubmissionStatus.rejected_staff)
        ) return new ProcessableCheckResponse(
                "submission already rejected by staff. Approve it if it's already correct",
                false
        );

        if (request.getSubmissionStatus() == SubmissionStatus.verified_staff && (
                submission.getLecturer() != null && submission.getStaff() == null && submission.getStatus() == SubmissionStatus.rejected_adhoc)
        ) return new ProcessableCheckResponse(
                "submission got rejected by adhoc and hasn't been verified yet. Wait until it gets approved by them",
                false
        );

        if (
                (submission.getLecturer() != null && submission.getLecturerVerifiedAt() != null) &&
                (submission.getStaff() != null && submission.getStaffVerifiedAt() != null)
        ) return new  ProcessableCheckResponse(
                "submission already verified.",
                false
        );

        return new ProcessableCheckResponse(
                "",
                true
        );
    }
}
