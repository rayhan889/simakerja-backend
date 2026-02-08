package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionType;

import java.time.Instant;

public class DocumentSubmissionDTO {
    private String submissionCode;
    private SubmissionType submissionType;
    private String status;
    private String notes;
    private String facultyLetterNumber;
    private String faculty;
    private Instant submissionDate;

    public String getSubmissionCode() {
        return submissionCode;
    }

    public void setSubmissionCode(String submissionCode) {
        this.submissionCode = submissionCode;
    }

    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(SubmissionType submissionType) {
        this.submissionType = submissionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}
