package com.rynrama.simakerjabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class GetPresignedUrlRequest {

    @NotBlank(message = "object key is required. Please upload first.")
    private String objectKey;

    public GetPresignedUrlRequest() {
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}
