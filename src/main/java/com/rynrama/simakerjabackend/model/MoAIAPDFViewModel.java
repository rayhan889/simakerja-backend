package com.rynrama.simakerjabackend.model;

import com.rynrama.simakerjabackend.dto.StudentSnapshotDisplayDTO;

import java.util.List;

public class MoAIAPDFViewModel implements PDFViewModel {
    private String facultyName;
    private String facultyRepresentativeName;
    private String facultyAddress;

    private String partnerName;
    private String partnerLogoUrl;
    private String partnerNumber;
    private String partnerRepresentativeName;
    private String partnerRepresentativePosition;
    private String partnerAddress;

    private String activityType;

    private String day;
    private String date;
    private String month;
    private String yearInLongText;
    private String ddMMyyyyFormatDate;

    private String unesaLogoUrl;

    private List<StudentSnapshotDisplayDTO> studentSnapshots;

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
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

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYearInLongText() {
        return yearInLongText;
    }

    public void setYearInLongText(String yearInLongText) {
        this.yearInLongText = yearInLongText;
    }

    public String getDdMMyyyyFormatDate() {
        return ddMMyyyyFormatDate;
    }

    public void setDdMMyyyyFormatDate(String ddMMyyyyFormatDate) {
        this.ddMMyyyyFormatDate = ddMMyyyyFormatDate;
    }

    public List<StudentSnapshotDisplayDTO> getStudentSnapshots() {
        return studentSnapshots;
    }

    public void setStudentSnapshots(List<StudentSnapshotDisplayDTO> studentSnapshots) {
        this.studentSnapshots = studentSnapshots;
    }

    public String getFacultyAddress() {
        return facultyAddress;
    }

    public void setFacultyAddress(String facultyAddress) {
        this.facultyAddress = facultyAddress;
    }

    public String getPartnerLogoUrl() {
        return partnerLogoUrl;
    }

    public void setPartnerLogoUrl(String partnerLogoUrl) {
        this.partnerLogoUrl = partnerLogoUrl;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(String partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public String getUnesaLogoUrl() {
        return unesaLogoUrl;
    }

    public void setUnesaLogoUrl(String unesaLogoUrl) {
        this.unesaLogoUrl = unesaLogoUrl;
    }
}
