package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.SubmissionStatus;

public class StaffSubmissionPaginationDetailDTO {
    private String submissionId;
    private String submissionCode;
    private String applicantStudyProgram;
    private String applicantFullname;
    private String applicantNim;
    private SubmissionStatus submissionStatus;

    public StaffSubmissionPaginationDetailDTO(String submissionId, String submissionCode, String applicantStudyProgram, String applicantFullname, String applicantNim, SubmissionStatus submissionStatus) {
        this.submissionId = submissionId;
        this.submissionCode = submissionCode;
        this.applicantStudyProgram = applicantStudyProgram;
        this.applicantFullname = applicantFullname;
        this.applicantNim = applicantNim;
        this.submissionStatus = submissionStatus;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getSubmissionCode() {
        return submissionCode;
    }

    public void setSubmissionCode(String submissionCode) {
        this.submissionCode = submissionCode;
    }

    public String getApplicantStudyProgram() {
        return applicantStudyProgram;
    }

    public void setApplicantStudyProgram(String applicantStudyProgram) {
        this.applicantStudyProgram = applicantStudyProgram;
    }

    public String getApplicantFullname() {
        return applicantFullname;
    }

    public void setApplicantFullname(String applicantFullname) {
        this.applicantFullname = applicantFullname;
    }

    public String getApplicantNim() {
        return applicantNim;
    }

    public void setApplicantNim(String applicantNim) {
        this.applicantNim = applicantNim;
    }

    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }
}
