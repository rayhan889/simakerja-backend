package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class DocumentUpdateRequest {
    private String notes;

    @Valid
    private MoAIADocumentUpdateRequest moaIa;

    public DocumentUpdateRequest() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MoAIADocumentUpdateRequest getMoaIa() {
        return moaIa;
    }

    public void setMoaIa(MoAIADocumentUpdateRequest moaIa) {
        this.moaIa = moaIa;
    }
}
