package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.FileUploadResponse;
import com.rynrama.simakerjabackend.service.MinioService;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/uploads")
public class FileUploadController {

    private final MinioService minioService;

    public FileUploadController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping(value = "/partner-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalAPIResponse<FileUploadResponse>> uploadPartnerLogo(
            @RequestPart("file") MultipartFile file
    ) {

        try {
            String objectKey = minioService.uploadPartnerLogo(file);

            String previewUrl = minioService.getPresignedUrl(objectKey);

            FileUploadResponse fileUploadResponse = new FileUploadResponse(objectKey, previewUrl);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(GlobalAPIResponse.success(fileUploadResponse, "file upload success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalAPIResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalAPIResponse.error(e.getMessage()));
        }

    }
}
