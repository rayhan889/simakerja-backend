package com.rynrama.simakerjabackend.dto;

import com.rynrama.simakerjabackend.model.DocumentActivityType;

import java.time.LocalDate;

public class StaffSubmissionPaginationDetailHeaderDTO {
    private String partnerName;
    private LocalDate period;
    private DocumentActivityType activityType;

    public StaffSubmissionPaginationDetailHeaderDTO(String partnerName, LocalDate period, DocumentActivityType activityType) {
        this.partnerName = partnerName;
        this.period = period;
        this.activityType = activityType;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public DocumentActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(DocumentActivityType activityType) {
        this.activityType = activityType;
    }
}
