package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionStatus;
import com.rynrama.simakerjabackend.model.SubmissionType;
import com.rynrama.simakerjabackend.model.UserModel;
import org.apache.catalina.User;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class DocumentSubmissionDTO implements DocumentDetails {
    private String id;
    private UserModel user;
    private String submissionCode;
    private SubmissionType submissionType;
    private SubmissionStatus status;
    private String notes;
    private String faculty;
    private Instant submissionDate;

    private String facultyLetterNumber;
    private String facultyAddress;
    private Instant createdAt;
    private Instant updatedAt;

    private MoAIADocumentDTO moaIa;

    public DocumentSubmissionDTO(
            String id,
            UserModel user,
            String submissionCode,
            SubmissionType submissionType,
            SubmissionStatus status,
            String notes,
            String faculty,
            Instant submissionDate
    ) {
        this.id = id;
        this.user = user;
        this.submissionCode = submissionCode;
        this.submissionType = submissionType;
        this.status = status;
        this.notes = notes;
        this.faculty = faculty;
        this.submissionDate = submissionDate;
    }

    public DocumentSubmissionDTO(String id, UserModel user, String submissionCode, SubmissionType submissionType, SubmissionStatus status, String notes, String faculty, Instant submissionDate, String facultyLetterNumber, String facultyAddress, Instant createdAt, Instant updatedAt, MoAIADocumentDTO moaIa) {
        this.id = id;
        this.user = user;
        this.submissionCode = submissionCode;
        this.submissionType = submissionType;
        this.status = status;
        this.notes = notes;
        this.faculty = faculty;
        this.submissionDate = submissionDate;
        this.facultyLetterNumber = facultyLetterNumber;
        this.facultyAddress = facultyAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.moaIa = moaIa;
    }

    public DocumentSubmissionDTO() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(SubmissionType submissionType) {
        this.submissionType = submissionType;
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

    public String getFacultyLetterNumber() {
        return facultyLetterNumber;
    }

    public void setFacultyLetterNumber(String facultyLetterNumber) {
        this.facultyLetterNumber = facultyLetterNumber;
    }

    public String getFacultyAddress() {
        return facultyAddress;
    }

    public void setFacultyAddress(String facultyAddress) {
        this.facultyAddress = facultyAddress;
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

    public MoAIADocumentDTO getMoaIa() {
        return moaIa;
    }

    public void setMoaIa(MoAIADocumentDTO moaIa) {
        this.moaIa = moaIa;
    }
}
