package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(FileCleanupService.class);

    private final MinioService minioService;
    private final MoAIADocumentRepository  moAIADocumentRepository;

    @Value("${cleanup.file.threshold-hours:24}")
    private int thresholdHours;

    @Value("${cleanup.file.enabled:true}")
    private boolean cleanupEnabled;

    public FileCleanupService(MinioService minioService, MoAIADocumentRepository moAIADocumentRepository) {
        this.minioService = minioService;
        this.moAIADocumentRepository = moAIADocumentRepository;
    }

//    @Scheduled(cron = "0 0 */6 * * ?")
    @Scheduled(cron =  "0 0 */6 * * ?") // test only
    public void cleanupPartnerLogos() {
        if (!cleanupEnabled) {
            logger.debug("cleanupPartnerLogos is disabled, skipping");
            return;
        }

        logger.info("starting cleanup partner logos...");

        try {
            List<String> minioKeys = minioService.listObjects("partner-logos/");
            if (minioKeys.isEmpty()) {
                logger.info("no partner logos need to cleanup, skipping");
                return;
            }
            logger.info("found {} total objects in minio partner-logos", minioKeys.size());

            Set<String> foundLogoKeys = new HashSet<>(moAIADocumentRepository.findAllPartnerLogoKeys());
            logger.info("found {} total partner logo key reference in database", minioKeys.size());

            List<String> unusedPartnerLogos = minioKeys.stream().
                    filter((key) -> !foundLogoKeys.contains(key))
                    .toList();
            logger.info("found {} total unused partner logos",  unusedPartnerLogos.size());

            Instant threshold = Instant.now().minus(Duration.ofHours(thresholdHours));
            int deletedCount = 0;

            for (String unusedPartnerLogo : unusedPartnerLogos) {
                try {
                    long lastModified = minioService.getObjectLastModified(unusedPartnerLogo);

                    if (lastModified == -1) {
                        logger.warn("couldn't determine age of {}, skipping", unusedPartnerLogo);
                    }

                    Instant objectTime = Instant.ofEpochMilli(lastModified);

                    if (objectTime.isBefore(threshold)) {
                        minioService.deleteObject(unusedPartnerLogo);
                        deletedCount++;
                        logger.debug("deleted {} partner-logos from minio", unusedPartnerLogo);
                    } else {
                        logger.debug("skipping deletion of {}. not fulfill the threshold yet",  unusedPartnerLogo);
                    }

                } catch (Exception e) {
                    logger.error("failed to proceed unused keys: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("cleanupPartnerLogos failed", e);
        }
    }

    public void manualCleanup() {
        logger.info("starting manual cleanup...");
        cleanupPartnerLogos();
    }
}
