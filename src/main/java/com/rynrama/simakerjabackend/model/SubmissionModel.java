package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity
@Table(
        name = "submissions",
        indexes = {
            @Index(name = "idx_submission_code", columnList = "submission_code"),
            @Index(name = "idx_user_id", columnList = "user_id"),
            @Index(name = "idx_status", columnList = "status"),
            @Index(name = "idx_submission_type", columnList = "submission_type")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_submission_code", columnNames = "submission_code")
        }
)
public class SubmissionModel {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "submission_code", unique = true, nullable = false, length = 50)
    private String submissionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", length = 50, nullable = false)
    private SubmissionType submissionType;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private SubmissionStatus status = SubmissionStatus.pending;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "faculty_letter_number")
    private String facultyLetterNumber;

    @Column(length = 50, nullable = false)
    private String faculty;

    @Column(name = "submission_date", nullable = false)
    private Instant submissionDate;

//    TO BE IMPLEMENTED LATER
//    private Instant adhocVerifiedAt;
//    private AdhocModel adhoc;
//    private Instant staffVerifiedAt;
//    private StaffModel staff;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public SubmissionModel() {
    }

    public SubmissionModel(String id, String submissionCode, UserModel user, SubmissionType submissionType, SubmissionStatus status, String notes, String facultyLetterNumber, String faculty, Instant submissionDate, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.submissionCode = submissionCode;
        this.user = user;
        this.submissionType = submissionType;
        this.status = status;
        this.notes = notes;
        this.facultyLetterNumber = facultyLetterNumber;
        this.faculty = faculty;
        this.submissionDate = submissionDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubmissionCode() {
        return submissionCode;
    }

    public void setSubmissionCode(String submissionCode) {
        this.submissionCode = submissionCode;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(SubmissionType type) {
        this.submissionType = type;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFacultyLetterNumber() {
        return facultyLetterNumber;
    }

    public void setFacultyLetterNumber(String facultyLetterNumber) {
        this.facultyLetterNumber = facultyLetterNumber;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Instant getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
