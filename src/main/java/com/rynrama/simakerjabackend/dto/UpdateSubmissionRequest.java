package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionStatus;
import com.rynrama.simakerjabackend.model.SubmissionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UpdateSubmissionRequest {

    @NotNull
    private SubmissionStatus submissionStatus;

    @Size(max = 1000)
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String notes;

    @AssertTrue(message = "notes(reason) are required when submission status is rejected")
    public boolean isRejectionValid() {
        if (submissionStatus == null) {
            return true;
        }

        if (submissionStatus == SubmissionStatus.rejected_staff || submissionStatus == SubmissionStatus.rejected_adhoc) {
            return notes != null;
        }
        return true;
    }
}
