package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "partner_number", length = 50)
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

    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StudentSnapshotModel> studentSnapshots = new ArrayList<>();

    @Column(name = "partner_logo_key", nullable = false)
    private String partnerLogoKey;

    @Column(name = "partner_address", nullable = false)
    private String partnerAddress;

    @Column(name = "partner_cooperation_period", nullable = false)
    private Integer partnerCooperationPeriod = 1;

    @Column(name = "scanned_document_key")
    private String scannedDocumentKey;

    @Column(name = "send_scanned_at")
    private Instant sendScannedAt;

    @Column(name = "scanned_ocr_confident_score")
    private Double scannedDocumentOcrConfidentScore = 0.0;

    public MoaIADocumentModel() {
    }

    public MoaIADocumentModel(
            UUID id,
            MoAIADocumentType documentType,
            String partnerName,
            String partnerNumber,
            String facultyRepresentativeName,
            String partnerRepresentativeName,
            String partnerRepresentativePosition,
            DocumentActivityType activityType,
            List<StudentSnapshotModel> studentSnapshots,
            String partnerLogoKey,
            String partnerAddress,
            Integer  partnerCooperationPeriod,
            String scannedDocumentKey,
            Instant sendScannedAt,
            Double scannedDocumentOcrConfidentScore
    ) {
        this.id = id;
        this.documentType = documentType;
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.facultyRepresentativeName = facultyRepresentativeName;
        this.partnerRepresentativeName = partnerRepresentativeName;
        this.partnerRepresentativePosition = partnerRepresentativePosition;
        this.activityType = activityType;
        this.studentSnapshots = studentSnapshots;
        this.partnerLogoKey = partnerLogoKey;
        this.partnerAddress = partnerAddress;
        this.partnerCooperationPeriod = partnerCooperationPeriod;
        this.scannedDocumentKey = scannedDocumentKey;
        this.sendScannedAt = sendScannedAt;
        this.scannedDocumentOcrConfidentScore = scannedDocumentOcrConfidentScore;
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

    public SubmissionModel getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionModel submission) {
        this.submission = submission;
    }

    public String getPartnerLogoKey() {
        return partnerLogoKey;
    }

    public void setPartnerLogoKey(String partnerLogoKey) {
        this.partnerLogoKey = partnerLogoKey;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(String partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public List<StudentSnapshotModel> getStudentSnapshots() {
        return studentSnapshots;
    }

    public void setStudentSnapshots(List<StudentSnapshotModel> studentSnapshots) {
        this.studentSnapshots = studentSnapshots;
    }

    public void clearStudentSnapshots() {
        for (StudentSnapshotModel s : studentSnapshots) {
            s.setDocument(null);
        }
        studentSnapshots.clear();
    }

    public void addStudentSnapshot(StudentSnapshotModel snapshot) {
        studentSnapshots.add(snapshot);
        snapshot.setDocument(this);
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

    public Instant getSendScannedAt() {
        return sendScannedAt;
    }

    public void setSendScannedAt(Instant sendScannedAt) {
        this.sendScannedAt = sendScannedAt;
    }

    public Double getScannedDocumentOcrConfidentScore() {
        return scannedDocumentOcrConfidentScore;
    }

    public void setScannedDocumentOcrConfidentScore(Double scannedDocumentOcrConfidentScore) {
        this.scannedDocumentOcrConfidentScore = scannedDocumentOcrConfidentScore;
    }
}
