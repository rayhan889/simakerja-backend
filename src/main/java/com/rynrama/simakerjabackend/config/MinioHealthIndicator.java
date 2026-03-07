package com.rynrama.simakerjabackend.config;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioHealthIndicator(MinioClient minioClient, @Value("${minio.bucket.name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public Health health() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (bucketExists) {
                return Health.up()
                        .withDetail("status", "MinIO is accessible")
                        .withDetail("bucket", bucketName)
                        .withDetail("bucketExists", true)
                        .build();
            } else {
                return Health.down()
                        .withDetail("status", "MinIO bucket does not exist")
                        .withDetail("bucket", bucketName)
                        .withDetail("bucketExists", false)
                        .build();
            }

        } catch (Exception e) {
            log.warn("MinIO health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("status", "MinIO is not accessible")
                    .withDetail("bucket", bucketName)
                    .withDetail("error", e.getMessage())
                    .withException(e)
                    .build();
        }
    }
}
