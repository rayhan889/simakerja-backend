package com.rynrama.simakerjabackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rynrama.simakerjabackend.model.DocumentActivityType;
import com.rynrama.simakerjabackend.model.MoAIADocumentType;
import com.rynrama.simakerjabackend.model.StudentSnapshot;

import java.util.List;

public class MoAIADocumentDTO implements DocumentDetails {
    private final String partnerName;
    private final String partnerNumber;
    private final String facultyRepresentativeName;
    private final String partnerRepresentativeName;
    private final String partnerRepresentativePosition;
    private final DocumentActivityType activityType;
    private final MoAIADocumentType documentType;
    private final List<StudentSnapshot> studentSnapshots;
    private final String partnerAddress;
    private final String partnerLogoKey;

    @JsonCreator
    public MoAIADocumentDTO(
            @JsonProperty("partnerName") String partnerName,
            @JsonProperty("partnerNumber") String partnerNumber,
            @JsonProperty("facultyRepresentativeName") String facultyRepresentativeName,
            @JsonProperty("partnerRepresentativeName") String partnerRepresentativeName,
            @JsonProperty("partnerRepresentativePosition") String partnerRepresentativePosition,
            @JsonProperty("activityType") DocumentActivityType activityType,
            @JsonProperty("documentType") MoAIADocumentType documentType,
            @JsonProperty("studentSnapshots") List<StudentSnapshot> studentSnapshots,
            @JsonProperty("partnerAddress") String partnerAddress,
            @JsonProperty("partnerLogoKey") String partnerLogoKey
    ) {
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.facultyRepresentativeName = facultyRepresentativeName;
        this.partnerRepresentativeName = partnerRepresentativeName;
        this.partnerRepresentativePosition = partnerRepresentativePosition;
        this.activityType = activityType;
        this.documentType = documentType;
        this.studentSnapshots = studentSnapshots;
        this.partnerAddress = partnerAddress;
        this.partnerLogoKey = partnerLogoKey;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getPartnerNumber() {
        return partnerNumber;
    }

    public String getFacultyRepresentativeName() {
        return facultyRepresentativeName;
    }

    public String getPartnerRepresentativeName() {
        return partnerRepresentativeName;
    }

    public String getPartnerRepresentativePosition() {
        return partnerRepresentativePosition;
    }

    public DocumentActivityType getActivityType() {
        return activityType;
    }

    public MoAIADocumentType getDocumentType() {
        return documentType;
    }

    public List<StudentSnapshot> getStudentSnapshots() {
        return studentSnapshots;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public String getPartnerLogoKey() {
        return partnerLogoKey;
    }
}
