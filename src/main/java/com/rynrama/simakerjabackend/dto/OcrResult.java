package com.rynrama.simakerjabackend.dto;

import java.util.List;

public class OcrResult {

    private final String extractedText;
    private final double averageConfidence;
    private final int totalPages;
    private final List<Double> pageConfidences;
    private final int anchorMatchCount;
    private final boolean templateMatched;

    public OcrResult(
            String extractedText,
            double averageConfidence,
            int totalPages,
            List<Double> pageConfidences,
            int anchorMatchCount,
            boolean templateMatched
    ) {
        this.extractedText = extractedText;
        this.averageConfidence = averageConfidence;
        this.totalPages = totalPages;
        this.pageConfidences = pageConfidences;
        this.anchorMatchCount = anchorMatchCount;
        this.templateMatched = templateMatched;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public double getAverageConfidence() {
        return averageConfidence;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Double> getPageConfidences() {
        return pageConfidences;
    }

    public int getAnchorMatchCount() {
        return anchorMatchCount;
    }

    public boolean isTemplateMatched() {
        return templateMatched;
    }
}
