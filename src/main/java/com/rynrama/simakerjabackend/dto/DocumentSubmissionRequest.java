package com.rynrama.simakerjabackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import com.rynrama.simakerjabackend.model.SubmissionType;
import jakarta.annotation.Nonnull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DocumentSubmissionRequest {
    @NotNull
    private SubmissionType submissionType;

    @Size(max = 1000)
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String notes;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "can't use any special characters")
    private String faculty;

    @Valid
    private MoaIADocumentRequest moaIa;

    public DocumentSubmissionRequest(
            @Nonnull SubmissionType submissionType,
            String notes,
            @Nonnull String faculty,
            MoaIADocumentRequest moaIa
    ) {
        this.submissionType = submissionType;
        this.notes = notes;
        this.faculty = faculty;
        this.moaIa = moaIa;
    }

    @AssertTrue(message = "moa_ia details are required when submission_type is moa_ia")
    public boolean isMoaIaValid() {
        if (submissionType == null) {
            return true;
        }

        if (submissionType == SubmissionType.moa_ia) {
            return moaIa != null;
        }
        return true;
    }

    @Nonnull
    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(@Nonnull SubmissionType submissionType) {
        this.submissionType = submissionType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Nonnull
    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(@Nonnull String faculty) {
        this.faculty = faculty;
    }

    public MoaIADocumentRequest getMoaIa() {
        return moaIa;
    }

    public void setMoaIa(MoaIADocumentRequest moaIa) {
        this.moaIa = moaIa;
    }
}
