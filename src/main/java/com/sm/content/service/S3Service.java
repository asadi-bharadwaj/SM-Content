package com.sm.content.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    /**
     * Generates a pre-signed URL for uploading a file to S3.
     * @param extension The file extension (e.g., "jpg", "mp4")
     * @param contentType The MIME type (e.g., "image/jpeg", "video/mp4")
     * @return An object containing the upload URL and the final S3 object key.
     */
    public PreSignedUrlResponse generatePreSignedUrl(String extension, String contentType) {
        String objectKey = "posts/" + UUID.randomUUID().toString() + "." + extension;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String url = presignedRequest.url().toString();

        log.info("Generated pre-signed URL for key: {}", objectKey);

        return new PreSignedUrlResponse(url, objectKey);
    }

    public static class PreSignedUrlResponse {
        private String uploadUrl;
        private String objectKey;

        public PreSignedUrlResponse(String uploadUrl, String objectKey) {
            this.uploadUrl = uploadUrl;
            this.objectKey = objectKey;
        }

        public String getUploadUrl() {
            return uploadUrl;
        }

        public String getObjectKey() {
            return objectKey;
        }
    }
}
