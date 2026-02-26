package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;

import java.time.LocalDate;

public class StaffSubmissionPaginationDTO {
    private LocalDate period;
    private String partnerName;
    private String partnerNumber;
    private DocumentActivityType activityType;
    private Long totalSubmissions;

    public StaffSubmissionPaginationDTO(LocalDate period, String partnerName, String partnerNumber, DocumentActivityType activityType, Number totalSubmissions) {
        this.period = period;
        this.partnerName = partnerName;
        this.partnerNumber = partnerNumber;
        this.activityType = activityType;
        this.totalSubmissions = totalSubmissions != null ? totalSubmissions.longValue() : null;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public DocumentActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(DocumentActivityType activityType) {
        this.activityType = activityType;
    }

    public Long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(Long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public String getPartnerNumber() {
        return partnerNumber;
    }

    public void setPartnerNumber(String partnerNumber) {
        this.partnerNumber = partnerNumber;
    }
}
