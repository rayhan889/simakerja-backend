package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "moa_ia_documents")
public class MoaIADocumentModel {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "submission_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_moa_ia_documents_submission")
    )
    private SubmissionModel submission;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private MoAIADocumentType documentType;

    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @Column(name = "partner_number", nullable = false, length = 50)
    private String partnerNumber;

    @Column(name = "faculty_representative_name", nullable = false)
    private String facultyRepresentativeName;

    @Column(name = "partner_representative_name", nullable = false)
    private String partnerRepresentativeName;

    @Column(name = "partner_representative_position", nullable = false)
    private String partnerRepresentativePosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_activity", nullable = false)
    private DocumentActivityType activityType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "student_snapshot", columnDefinition = "jsonb", nullable = false)
    private StudentSnapshot studentSnapshot;

    public MoaIADocumentModel() {
    }

    public MoaIADocumentModel(
            UUID id, MoAIADocumentType documentType, String partnerName, String partnerNumber,
            String facultyRepresentativeName, String partnerRepresentativeName, String partnerRepresentativePosition,
            DocumentActivityType activityType, StudentSnapshot studentSnapshot
    ) {
        this.id = id;
        this.documentType = documentType;
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.facultyRepresentativeName = facultyRepresentativeName;
        this.partnerRepresentativeName = partnerRepresentativeName;
        this.partnerRepresentativePosition = partnerRepresentativePosition;
        this.activityType = activityType;
        this.studentSnapshot = studentSnapshot;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public SubmissionModel getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionModel submission) {
        this.submission = submission;
    }
}
