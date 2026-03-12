package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AdhocSubmissionPaginationDetailDTO {
    private String submissionId;
    private String submissionCode;
    private String applicantFullname;
    private String applicantNim;
    private SubmissionStatus submissionStatus;
}
