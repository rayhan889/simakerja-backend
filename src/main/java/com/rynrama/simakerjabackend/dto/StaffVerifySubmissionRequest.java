package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionStatus;
import jakarta.validation.constraints.NotNull;

public class StaffVerifySubmissionRequest {

    @NotNull
    private SubmissionStatus submissionStatus;

    public StaffVerifySubmissionRequest() {
    }

    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }
}
