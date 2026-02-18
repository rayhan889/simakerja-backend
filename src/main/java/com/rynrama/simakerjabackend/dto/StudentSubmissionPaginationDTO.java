package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;
import com.rynrama.simakerjabackend.model.MoAIADocumentType;
import com.rynrama.simakerjabackend.model.SubmissionStatus;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class StudentSubmissionPaginationDTO {
    private String submissionId;
    private String partnerName;
    private String partnerNumber;
    private SubmissionStatus status;
    private DocumentActivityType activityType;
    private Instant submissionDate;
    private String notes;
    private MoAIADocumentType documentType;

    public StudentSubmissionPaginationDTO(
            String submissionId,
            String partnerName,
            String partnerNumber,
            SubmissionStatus status,
            DocumentActivityType activityType,
            Instant submissionDate,
            String notes,
            MoAIADocumentType documentType
    ) {
        this.submissionId = submissionId;
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.status = status;
        this.activityType = activityType;
        this.submissionDate = submissionDate;
        this.notes = notes;
        this.documentType = documentType;
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
}
