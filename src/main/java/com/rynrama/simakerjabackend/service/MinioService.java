package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.OcrResult;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    private final MinioClient minioClient;

    private final MinioClient presignedMinioClient;

    private final OcrService ocrService;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.presigned.expiry}")
    private int presignedExpiryMinutes;

    @Value("${minio.url}")
    private String internalUrl;

    @Value("${minio.public.url:#{null}}")
    private String publicUrl;

    private static final long SCANNED_DOCUMENT_MAX_SIZE = 1024 * 1024; // 1MB
    private static final long PARTNER_LOGO_MAX_SIZE = 1024 * 1024; // 1MB
    private static final double MINIMUM_CONFIDENCE_THRESHOLD = 60.0;

    public MinioService(
            MinioClient minioClient,
            @Qualifier("presignedMinioClient") MinioClient presignedMinioClient,
            OcrService ocrService
    ) {
        this.minioClient = minioClient;
        this.presignedMinioClient = presignedMinioClient;
        this.ocrService = ocrService;
    }

    public String uploadPartnerLogo(MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > PARTNER_LOGO_MAX_SIZE) {
            throw new IllegalArgumentException("File too large");
        }

        String contentType = file.getContentType();
        if (!List.of("image/jpeg", "image/png").contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Invalid image file");
        }

        String objectKey = "partner-logos" + "/" + UUID.randomUUID();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );

        logger.info("Uploaded partner logo to Minio: {}", objectKey);

        return objectKey;
    }

    public ScannedDocumentUploadResult uploadScannedDocument(MultipartFile file, String submissionId) throws Exception {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Invalid file type. Expected PDF, got: " + file.getContentType()
            );
        }

        if (file.getSize() > SCANNED_DOCUMENT_MAX_SIZE) {
            throw new IllegalArgumentException(
                    "File too large. Maximum size is 1MB, got: "
                            + String.format("%.2f MB", file.getSize() / (1024.0 * 1024.0))
            );
        }

        byte[] pdfBytes = file.getBytes();

        logger.info("Starting OCR validation for submission {}. File size: {} bytes",
                submissionId, pdfBytes.length);

        OcrResult ocrResult = ocrService.processDocument(pdfBytes);

        if (ocrResult.getAverageConfidence() < MINIMUM_CONFIDENCE_THRESHOLD) {
            throw new IllegalArgumentException(
                    String.format(
                            "Document scan quality is too low. Confidence: %.1f%% (minimum: %.1f%%). "
                                    + "Please re-scan with better clarity.",
                            ocrResult.getAverageConfidence(),
                            MINIMUM_CONFIDENCE_THRESHOLD
                    )
            );
        }

        if (!ocrResult.isTemplateMatched()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Document does not match the expected MoA/IA template. "
                                    + "Please upload the correct document."
                    )
            );
        }
        String objectKey = "scanned-documents/" + submissionId + "/" + UUID.randomUUID() + ".pdf";

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .stream(new ByteArrayInputStream(pdfBytes), pdfBytes.length, -1)
                        .contentType(MediaType.APPLICATION_PDF_VALUE)
                        .build()
        );

        logger.info("Uploaded scanned document to MinIO: {} (confidence: {}%, anchors: {}/{})",
                objectKey, String.format("%.1f", ocrResult.getAverageConfidence()),
                ocrResult.getAnchorMatchCount(), 7);

        return new ScannedDocumentUploadResult(objectKey, ocrResult);
    }

    public record ScannedDocumentUploadResult(String objectKey, OcrResult ocrResult) {}

//    public presigned URL - for browser
    public String getPresignedUrl(String objectKey) throws Exception {
        return presignedMinioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectKey)
                        .expiry(presignedExpiryMinutes)
                        .build()
        );
    }

//    internal presigned URL - for server side consumers to easily determined by docker network
    public String getInternalPresignedUrl(String objectKey) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectKey)
                        .expiry(presignedExpiryMinutes)
                        .build()
        );
    }

    public void deleteObject(String objectKey) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .build()
        );
        logger.info("Deleted object from Minio: {}", objectKey);
    }

    public List<String> listObjects(String prefix) {
        List<String> objectKeys = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(true)
                        .build()
        );

        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                objectKeys.add(item.objectName());
            } catch (Exception e) {
                logger.warn("Failed to get item info: {}", e.getMessage());
            }
        }

        return objectKeys;
    }

    public long getObjectLastModified(String objectKey) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            return stat.lastModified().toInstant().toEpochMilli();
        } catch (Exception e) {
            logger.warn("Failed to get object stats for {}: {}", objectKey, e.getMessage());
            return -1;
        }
    }
}
