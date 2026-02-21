package com.rynrama.simakerjabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class GetPresignedUrlRequest {

    @NotBlank(message = "Partner logo is required. Please upload a logo first.")
    @Pattern(
            regexp = "^partner-logos/[a-f0-9\\-]{36}$",
            message = "Invalid logo key format. Must be obtained from upload endpoint."
    )
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
