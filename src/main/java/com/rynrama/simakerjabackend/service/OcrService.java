package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.OcrResult;
import jakarta.annotation.PostConstruct;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OcrService {

    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);

    private static final List<String> ANCHOR_KEYWORDS = List.of(
            "PERJANJIAN KERJA SAMA",
            "MEMORANDUM OF AGREEMENT",
            "IMPLEMENTATION ARRANGEMENT",
            "PIHAK KESATU",
            "PIHAK KEDUA",
            "UNIVERSITAS NEGERI SURABAYA",
            "FAKULTAS TEKNIK"
    );

    private static final int MINIMUM_ANCHOR_MATCHES = 5;

    private final String tessdataPath;

    private final int ocrDpi;

    public OcrService(
            @Value("${ocr.tessdata.path:tessdata}") String tessdataPath,
            @Value("${ocr.dpi:300}") int ocrDpi
    ) {
        this.tessdataPath = tessdataPath;
        this.ocrDpi = ocrDpi;
    }

    @PostConstruct
    public void validateTessdataOnStartup() {
        logger.info("Validating tessdata on startup");

        logger.info("OCR config. Tessdata path: {}, DPI: {}", tessdataPath, ocrDpi);

        Path tessdataDir =  Paths.get(tessdataPath);
        if (!Files.isDirectory(tessdataDir)) {
            logger.error("Tessdata directtory not found");
            return;
        }

        Path indTrainedData = tessdataDir.resolve("ind.traineddata");
        if (Files.exists(indTrainedData)) {
            try {
                long sizeBytes = Files.size(indTrainedData);
                logger.info("Found ind.traineddata ({} bytes) at {}", sizeBytes, indTrainedData);
            } catch (IOException e) {
                logger.warn("Could not read ind.traineddata size: {}", e.getMessage());
            }
        } else {
            logger.error("ind.traineddata NOT FOUND at {}. OCR will fail!", indTrainedData);
        }

        try {
            Tesseract test = new Tesseract();
            test.setDatapath(tessdataPath);
            test.setLanguage("ind");
            logger.info("Tesseract native library loaded successfully");
        } catch (Throwable t) {
            logger.error("CRITICAL: Failed to initialize Tesseract native libraries. " +
                    "Ensure libleptonica-dev and libtesseract-dev are installed. " +
                    "Error: {}", t.getMessage(), t);
        }
    }

    public OcrResult processDocument(byte[] pdfBytes) throws IOException, TesseractException {
        Tesseract tesseract = createTesseractInstance();

        List<String> pageTexts = new ArrayList<>();
        List<Double> pageConfidences = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();


            if (pageCount > 0) {
                int firstPage = 0;
                logger.info("Starting OCR processing for first page only at {} DPI", ocrDpi);

                BufferedImage pageImage = renderer.renderImageWithDPI(firstPage, ocrDpi, ImageType.GRAY);

                List<Word> words = tesseract.getWords(pageImage, ITessAPI.TessPageIteratorLevel.RIL_WORD);

                String pageText = words.stream()
                        .map(Word::getText)
                        .map(String::trim)
                        .filter(text -> !text.isEmpty())
                        .collect(Collectors.joining(" "));

                double pageConfidence = words.stream()
                        .mapToDouble(Word::getConfidence)
                        .average()
                        .orElse(0.0);

                pageTexts.add(pageText);
                pageConfidences.add(pageConfidence);

                pageImage.flush();
            }
        }

        String fullText = String.join("\n", pageTexts);

        double averageConfidence = pageConfidences.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        String upperText = fullText.toUpperCase();

        List<String> matchedAnchors = ANCHOR_KEYWORDS.stream()
                .filter(keyword -> upperText.contains(keyword.toUpperCase()))
                .collect(Collectors.toList());

        int anchorMatchCount = matchedAnchors.size();
        boolean templateMatched = anchorMatchCount >= MINIMUM_ANCHOR_MATCHES;

        logger.info("OCR complete — {} page(s), avg confidence: {}%, anchors matched: {}/{} ({})",
                pageTexts.size(), String.format("%.1f", averageConfidence),
                anchorMatchCount, ANCHOR_KEYWORDS.size(),
                templateMatched ? "PASS" : "FAIL");

        if (logger.isDebugEnabled()) {
            logger.debug("Matched anchors: {}", matchedAnchors);
            logger.debug("Missing anchors: {}",
                    ANCHOR_KEYWORDS.stream()
                            .filter(k -> !upperText.contains(k.toUpperCase()))
                            .collect(Collectors.toList()));
        }

        return new OcrResult(
                fullText,
                averageConfidence,
                pageTexts.size(),
                pageConfidences,
                anchorMatchCount,
                templateMatched
        );
    }

    private Tesseract createTesseractInstance() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage("ind");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);
        return tesseract;
    }
}
