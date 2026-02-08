package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionStatus;
import com.rynrama.simakerjabackend.model.SubmissionType;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class DocumentSubmissionDTO {
    private String id;
    private SubmissionType submissionType;
    private SubmissionStatus status;
    private String notes;
    private String faculty;
    private Instant submissionDate;


    public DocumentSubmissionDTO(
            String id,
            SubmissionType submissionType, SubmissionStatus status,
            String notes, String faculty, Instant submissionDate
    ) {
        this.id = id;
        this.submissionType = submissionType;
        this.status = status;
        this.notes = notes;
        this.faculty = faculty;
        this.submissionDate = submissionDate;
    }

    public DocumentSubmissionDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(SubmissionType submissionType) {
        this.submissionType = submissionType;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Instant getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }
}
