package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.FileUploadResponse;
import com.rynrama.simakerjabackend.dto.GetPresignedUrlRequest;
import com.rynrama.simakerjabackend.dto.OcrResult;
import com.rynrama.simakerjabackend.service.MinioService;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping("/partner-logo/get-url")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF')")
    public ResponseEntity<GlobalAPIResponse<String>> getPresignedUrl(
            @Valid @RequestBody GetPresignedUrlRequest request
    ) throws Exception {

        String presignedUrl = minioService.getPresignedUrl(request.getObjectKey());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(presignedUrl));
    }

    @PostMapping(value = "/scanned-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'STAFF')")
    public ResponseEntity<GlobalAPIResponse<FileUploadResponse>> uploadScannedDocument(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "submission_id")  String submissionId
    ) {
        try {
            var result = minioService.uploadScannedDocument(file, submissionId);

            String previewUrl = minioService.getPresignedUrl(result.objectKey());

            FileUploadResponse fileUploadResponse = new FileUploadResponse(result.objectKey(), previewUrl, result.ocrResult().getAverageConfidence());

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
