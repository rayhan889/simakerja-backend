package com.rynrama.simakerjabackend.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);
    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.presigned.expiry}")
    private int presignedExpiryMinutes;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadPartnerLogo(MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
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

    public String getPresignedUrl(String objectKey) throws Exception {
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
