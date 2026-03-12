package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AdhocSubmissionPaginationDTO {
    private LocalDate period;
    private String partnerName;
    private String partnerNumber;
    private DocumentActivityType activityType;
    private Long totalSubmissions;
}
