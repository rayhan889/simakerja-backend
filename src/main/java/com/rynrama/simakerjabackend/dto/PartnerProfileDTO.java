package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class PartnerProfileDTO {
    private String partnerName;
    private String partnerAddress;
    private String partnerNumber;
    private String partnerRepresentativeName;
    private String partnerRepresentativePosition;
    private DocumentActivityType activityType;
    private String partnerLogoKey;
    private String facultyRepresentativeName;
    
    public PartnerProfileDTO(
            String partnerName, 
            String partnerAddress, 
            String partnerNumber, 
            String partnerRepresentativeName, 
            String partnerRepresentativePosition, 
            DocumentActivityType activityType, 
            String partnerLogoKey,
            String facultyRepresentativeName
    ) {
        this.partnerName = partnerName;
        this.partnerAddress = partnerAddress;
        this.partnerNumber = partnerNumber;
        this.partnerRepresentativeName = partnerRepresentativeName;
        this.partnerRepresentativePosition = partnerRepresentativePosition;
        this.activityType = activityType;
        this.partnerLogoKey = partnerLogoKey;
        this.facultyRepresentativeName = facultyRepresentativeName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(String partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public String getPartnerNumber() {
        return partnerNumber;
    }

    public void setPartnerNumber(String partnerNumber) {
        this.partnerNumber = partnerNumber;
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

    public String getPartnerLogoKey() {
        return partnerLogoKey;
    }

    public void setPartnerLogoKey(String partnerLogoKey) {
        this.partnerLogoKey = partnerLogoKey;
    }

    public String getFacultyRepresentativeName() {
        return facultyRepresentativeName;
    }

    public void setFacultyRepresentativeName(String facultyRepresentativeName) {
        this.facultyRepresentativeName = facultyRepresentativeName;
    }
}
