package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.StaffVerifySubmissionRequest;
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
    public SubmissionModel verifySubmissionByStaff(
            String submissionId,
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

        if (!isVerifiable(submission)) {
            log.warn("Submission={} is not verifiable", submissionId);
            throw new BadRequestException("submission cannot be verified by staff anymore");
        }

        StaffModel staff = staffRepo
                .findByUserId(staffUserId)
                .orElseThrow(() -> {
                    log.warn("Cannot find staff with userId={}", staffUserId);
                    return new ResourceNotFoundException(
                            "Staff with userId: " + staffUserId + " not found"
                    );
                });

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

        Instant now = Instant.now();
        submission.setStatus(SubmissionStatus.verified_staff);
        submission.setStaff(staff);
        submission.setStaffVerifiedAt(now);

        log.info("Submission={} has been verified", submissionId);
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

    private static Boolean isVerifiable(SubmissionModel submission) {
        if (submission == null) return false;

        if (submission.getStaff() != null || submission.getStaffVerifiedAt() != null) return false;

        return !submission.getStatus().equals(SubmissionStatus.verified_staff);
    }
}
