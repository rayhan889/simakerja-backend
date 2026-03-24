package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;
import com.rynrama.simakerjabackend.model.MoAIADocumentType;
import com.rynrama.simakerjabackend.model.SubmissionStatus;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class StudentSubmissionPaginationDTO {
    private UUID applicantId;
    private String applicantName;
    private String applicantNim;
    private String submissionId;
    private String partnerName;
    private String partnerNumber;
    private SubmissionStatus status;
    private DocumentActivityType activityType;
    private Instant submissionDate;
    private String notes;
    private MoAIADocumentType documentType;
    private LocalDate period;

    public StudentSubmissionPaginationDTO(
            UUID applicantId,
            String applicantName,
            String applicantNim,
            String submissionId,
            String partnerName,
            String partnerNumber,
            SubmissionStatus status,
            DocumentActivityType activityType,
            Instant submissionDate,
            String notes,
            MoAIADocumentType documentType,
            LocalDate period
    ) {
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.applicantNim = applicantNim;
        this.submissionId = submissionId;
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.status = status;
        this.activityType = activityType;
        this.submissionDate = submissionDate;
        this.notes = notes;
        this.documentType = documentType;
        this.period = period;
    }

    public StudentSubmissionPaginationDTO() {
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public void setPartnerNumber(String partnerNumber) {
        this.partnerNumber = partnerNumber;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public void setActivityType(DocumentActivityType activityType) {
        this.activityType = activityType;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getPartnerNumber() {
        return partnerNumber;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public DocumentActivityType getActivityType() {
        return activityType;
    }

    public Instant getSubmissionDate() {
        return submissionDate;
    }

    public String getNotes() {
        return notes;
    }

    public MoAIADocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(MoAIADocumentType documentType) {
        this.documentType = documentType;
    }

    public UUID getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(UUID applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantNim() {
        return applicantNim;
    }

    public void setApplicantNim(String applicantNim) {
        this.applicantNim = applicantNim;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }
}
