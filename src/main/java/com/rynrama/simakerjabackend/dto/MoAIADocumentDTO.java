package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;
import com.rynrama.simakerjabackend.model.MoAIADocumentType;
import com.rynrama.simakerjabackend.model.StudentSnapshot;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class MoAIADocumentDTO {
    private String partnerName;
    private String partnerNumber;
    private String facultyRepresentativeName;
    private String partnerRepresentativeName;
    private String partnerRepresentativePosition;
    private List<StudentSnapshot> studentSnapshots;
    private DocumentActivityType documentActivity;
    private MoAIADocumentType documentType;

    public MoAIADocumentDTO() {
    }

    public MoAIADocumentDTO(String partnerName, String partnerNumber, String facultyRepresentativeName, String partnerRepresentativeName, String partnerRepresentativePosition, List<StudentSnapshot> studentSnapshots, DocumentActivityType documentActivity, MoAIADocumentType documentType) {
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.facultyRepresentativeName = facultyRepresentativeName;
        this.partnerRepresentativeName = partnerRepresentativeName;
        this.partnerRepresentativePosition = partnerRepresentativePosition;
        this.studentSnapshots = studentSnapshots;
        this.documentActivity = documentActivity;
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

    public List<StudentSnapshot> getStudentSnapshots() {
        return studentSnapshots;
    }

    public void setStudentSnapshots(List<StudentSnapshot> studentSnapshots) {
        this.studentSnapshots = studentSnapshots;
    }

    public DocumentActivityType getDocumentActivity() {
        return documentActivity;
    }

    public void setDocumentActivity(DocumentActivityType documentActivity) {
        this.documentActivity = documentActivity;
    }

    public MoAIADocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(MoAIADocumentType documentType) {
        this.documentType = documentType;
    }
}
