package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;
import com.rynrama.simakerjabackend.model.MoAIADocumentType;
import com.rynrama.simakerjabackend.model.StudentSnapshot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoaIADocumentRequest {

    @NotNull
    private MoAIADocumentType documentType;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String partnerName;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String partnerNumber;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String facultyRepresentativeName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String partnerRepresentativeName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String partnerRepresentativePosition;

    @NotNull
    private DocumentActivityType  activityType;

    @Valid
    private StudentSnapshot studentSnapshot;

    public MoaIADocumentRequest(
            MoAIADocumentType documentType, String partnerName, String partnerNumber,
            String facultyRepresentativeName, String partnerRepresentativeName, String partnerRepresentativePosition,
            DocumentActivityType activityType, StudentSnapshot studentSnapshot
    ) {
        this.documentType = documentType;
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.facultyRepresentativeName = facultyRepresentativeName;
        this.partnerRepresentativeName = partnerRepresentativeName;
        this.partnerRepresentativePosition = partnerRepresentativePosition;
        this.activityType = activityType;
        this.studentSnapshot = studentSnapshot;
    }

    public MoAIADocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(MoAIADocumentType documentType) {
        this.documentType = documentType;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerNumber() {
        return partnerNumber;
    }

    public void setPartnerNumber(String partnerNumber) {
        this.partnerNumber = partnerNumber;
    }

    public String getFacultyRepresentativeName() {
        return facultyRepresentativeName;
    }

    public void setFacultyRepresentativeName(String facultyRepresentativeName) {
        this.facultyRepresentativeName = facultyRepresentativeName;
    }

    public String getPartnerRepresentativeName() {
        return partnerRepresentativeName;
    }

    public void setPartnerRepresentativeName(String partnerRepresentativeName) {
        this.partnerRepresentativeName = partnerRepresentativeName;
    }

    public String getPartnerRepresentativePosition() {
        return partnerRepresentativePosition;
    }

    public void setPartnerRepresentativePosition(String partnerRepresentativePosition) {
        this.partnerRepresentativePosition = partnerRepresentativePosition;
    }

    public DocumentActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(DocumentActivityType activityType) {
        this.activityType = activityType;
    }

    public StudentSnapshot getStudentSnapshot() {
        return studentSnapshot;
    }

    public void setStudentSnapshot(StudentSnapshot studentSnapshot) {
        this.studentSnapshot = studentSnapshot;
    }
}
