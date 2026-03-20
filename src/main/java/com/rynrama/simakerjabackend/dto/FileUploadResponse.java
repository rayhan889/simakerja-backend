package com.rynrama.simakerjabackend.dto;

public class FileUploadResponse {

    private String objectKey;
    private String previewUrl;
    private double averageConfidence;

    public FileUploadResponse(String objectKey, String previewUrl) {
        this.objectKey = objectKey;
        this.previewUrl = previewUrl;
    }

    public FileUploadResponse(String objectKey, String previewUrl,  double averageConfidence) {
        this.objectKey = objectKey;
        this.previewUrl = previewUrl;
        this.averageConfidence = averageConfidence;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public double getAverageConfidence() {
        return averageConfidence;
    }

    public void setAverageConfidence(double averageConfidence) {
        this.averageConfidence = averageConfidence;
    }
}
