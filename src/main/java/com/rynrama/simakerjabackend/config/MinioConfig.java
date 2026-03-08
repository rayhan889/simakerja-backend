package com.rynrama.simakerjabackend.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access.key}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String accessSecret;

    @Value("${minio.ssl}")
    private Boolean ssl;

    @Value("${minio.public.url:#{null}}")
    private String publicUrl;

    @Bean
    public boolean ensureBucketExists(
            MinioClient minioClient, @Value("${minio.bucket.name}") String bucketName
    ) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        return exists;
    }

    @Primary
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, accessSecret)
                .build();
    }

    @Bean(name = "presignedMinioClient")
    public MinioClient presignedMinioClient() {
        String endpoint = (publicUrl != null && !publicUrl.isBlank()) ? publicUrl : url;
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, accessSecret)
                .build();
    }

}
