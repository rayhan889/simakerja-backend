package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "submissions",
        indexes = {
            @Index(name = "idx_submission_code", columnList = "submission_code"),
            @Index(name = "idx_user_id", columnList = "user_id"),
            @Index(name = "idx_staff_id", columnList = "staff_id"),
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
    private SubmissionStatus status = SubmissionStatus.in_process;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "faculty_letter_number")
    private String facultyLetterNumber;

    @Column(length = 50, nullable = false)
    private String faculty;

    @Column(name = "submission_date", nullable = false)
    private Instant submissionDate;

    @Column(name = "faculty_address", nullable = false)
    private String facultyAddress;

    @Column(name = "staff_verified_at")
    private Instant staffVerifiedAt;

    @Column(name = "staff_rejected_at")
    private Instant staffRejectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private StaffModel staff;

    @Column(name = "lecturer_verified_at")
    private Instant lecturerVerifiedAt;

    @Column(name = "lecturer_rejected_at")
    private Instant lecturerRejectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private LecturerModel lecturer;

    @Column(name = "period", nullable = false)
    private LocalDate period;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public SubmissionModel() {
    }

    public SubmissionModel(
            String id,
            String submissionCode,
            UserModel user,
            SubmissionType submissionType,
            SubmissionStatus status,
            String notes,
            String facultyLetterNumber,
            String faculty,
            Instant submissionDate,
            String facultyAddress ,
            Instant staffVerifiedAt,
            Instant staffRejectedAt,
            StaffModel staff,
            Instant lecturerVerifiedAt,
            Instant lecturerRejectedAt,
            LecturerModel lecturer,
            LocalDate period,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.submissionCode = submissionCode;
        this.user = user;
        this.submissionType = submissionType;
        this.status = status;
        this.notes = notes;
        this.facultyLetterNumber = facultyLetterNumber;
        this.faculty = faculty;
        this.submissionDate = submissionDate;
        this.facultyAddress = facultyAddress;
        this.staffVerifiedAt = staffVerifiedAt;
        this.staffRejectedAt = staffRejectedAt;
        this.staff = staff;
        this.lecturerVerifiedAt = lecturerVerifiedAt;
        this.lecturerRejectedAt = lecturerRejectedAt;
        this.lecturer = lecturer;
        this.period = period;
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

    public String getFacultyAddress() {
        return facultyAddress;
    }

    public void setFacultyAddress(String facultyAddress) {
        this.facultyAddress = facultyAddress;
    }

    public Instant getStaffVerifiedAt() {
        return staffVerifiedAt;
    }

    public void setStaffVerifiedAt(Instant staffVerifiedAt) {
        this.staffVerifiedAt = staffVerifiedAt;
    }

    public StaffModel getStaff() {
        return staff;
    }

    public void setStaff(StaffModel staff) {
        this.staff = staff;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public Instant getLecturerVerifiedAt() {
        return lecturerVerifiedAt;
    }

    public void setLecturerVerifiedAt(Instant lecturerVerifiedAt) {
        this.lecturerVerifiedAt = lecturerVerifiedAt;
    }

    public LecturerModel getLecturer() {
        return lecturer;
    }

    public void setLecturer(LecturerModel lecturer) {
        this.lecturer = lecturer;
    }

    public Instant getLecturerRejectedAt() {
        return lecturerRejectedAt;
    }

    public void setLecturerRejectedAt(Instant lecturerRejectedAt) {
        this.lecturerRejectedAt = lecturerRejectedAt;
    }

    public Instant getStaffRejectedAt() {
        return staffRejectedAt;
    }

    public void setStaffRejectedAt(Instant staffRejectedAt) {
        this.staffRejectedAt = staffRejectedAt;
    }
}
