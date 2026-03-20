package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;
import com.rynrama.simakerjabackend.model.MoAIADocumentType;
import com.rynrama.simakerjabackend.model.StudentSnapshot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class MoAIADocumentUpdateRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 .(),-]*$", message = "can't use any special characters")
    private String partnerName;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String partnerNumber;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String facultyRepresentativeName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String partnerRepresentativeName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 .,-]*$", message = "can't use any special characters")
    private String partnerRepresentativePosition;

    @NotNull
    private DocumentActivityType activityType;

    @Valid
    private List<StudentSnapshot> studentSnapshots;

    @NotBlank(message = "Partner address is required")
    private String partnerAddress;

    @NotBlank(message = "Partner logo is required. Please upload a logo first.")
    @Pattern(
            regexp = "^partner-logos/[a-f0-9\\-]{36}$",
            message = "Invalid logo key format. Must be obtained from upload endpoint."
    )
    private String partnerLogoKey;

    @NotNull
    @Min(1)
    @Max(7)
    private Integer partnerCooperationPeriod;

    @Pattern(
            regexp = "^scanned-documents/[a-f0-9\\-]{36}/[a-f0-9\\-]{36}\\.pdf$",
            message = "Invalid document key format. Must be a valid scanned-document path."
    )
    private String scannedDocumentKey;

    private double averageConfidence;

    public MoAIADocumentUpdateRequest() {
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

    public List<StudentSnapshot> getStudentSnapshots() {
        return studentSnapshots;
    }

    public void setStudentSnapshots(List<StudentSnapshot> studentSnapshots) {
        this.studentSnapshots = studentSnapshots;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(String partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public String getPartnerLogoKey() {
        return partnerLogoKey;
    }

    public void setPartnerLogoKey(String partnerLogoKey) {
        this.partnerLogoKey = partnerLogoKey;
    }

    public Integer getPartnerCooperationPeriod() {
        return partnerCooperationPeriod;
    }

    public void setPartnerCooperationPeriod(Integer partnerCooperationPeriod) {
        this.partnerCooperationPeriod = partnerCooperationPeriod;
    }

    public String getScannedDocumentKey() {
        return scannedDocumentKey;
    }

    public void setScannedDocumentKey(String scannedDocumentKey) {
        this.scannedDocumentKey = scannedDocumentKey;
    }

    public double getAverageConfidence() {
        return averageConfidence;
    }

    public void setAverageConfidence(double averageConfidence) {
        this.averageConfidence = averageConfidence;
    }
}
