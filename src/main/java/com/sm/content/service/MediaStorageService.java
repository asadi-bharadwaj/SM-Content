package com.sm.content.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service for storing media files (images/videos) either on AWS S3 or the local filesystem.
 *
 * <p>Flow: Determines the storage target based on the presence of the 'aws.s3.bucket' property.
 * Generates a unique filename and uploads the file to the chosen storage provider.
 *
 * <p>Features: Hybrid storage support (S3 or Local), automatic extension resolution, and public URL generation.
 */
@Service
public class MediaStorageService {

  @Value("${aws.s3.bucket:}")
  private String s3Bucket;

  @Value("${aws.s3.region:eu-north-1}")
  private String s3Region;

  @Value("${content.upload.directory:./data/uploads}")
  private String uploadDirectory;

  private volatile S3Client s3Client;

  /**
   * Stores a multipart file and returns its public URL.
   *
   * <p>Flow: Resolves the file extension and generates a unique name. If S3 is configured, it
   * uploads to S3 using the AWS SDK. Otherwise, it saves the file to the local upload directory.
   *
   * <p>Features: Secure file storage with unique naming to prevent collisions.
   *
   * @param file The multipart file to store
   * @param authorId The ID of the author (used for S3 key partitioning)
   * @param httpOriginBase e.g. http://localhost:8084 (no trailing slash) for local storage URL building
   * @return The full public URL to the stored object
   * @throws IOException If an error occurs during file transfer or S3 upload
   */
  public String store(MultipartFile file, long authorId, String httpOriginBase) throws IOException {
    String ext = resolveExtension(file);
    String name = UUID.randomUUID() + ext;
    String key = "posts/" + authorId + "/" + name;

    if (StringUtils.hasText(s3Bucket)) {
      S3Client s3 = s3();
      String contentType =
          file.getContentType() != null ? file.getContentType() : "application/octet-stream";
      PutObjectRequest put =
          PutObjectRequest.builder()
              .bucket(s3Bucket)
              .key(key)
              .contentType(contentType)
              .contentLength(file.getSize())
              .build();
      s3.putObject(put, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
      return virtualHostedStylePublicUrl(key);
    }

    Path dir = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    Files.createDirectories(dir);
    Path target = dir.resolve(name);
    file.transferTo(target);

    String base = httpOriginBase.endsWith("/") ? httpOriginBase.substring(0, httpOriginBase.length() - 1) : httpOriginBase;
    return base + "/media/" + name;
  }

  /**
   * Initializes and returns a singleton S3Client.
   *
   * <p>Flow: Uses double-checked locking to ensure a single S3Client is created using default
   * credentials and configured region.
   *
   * <p>Features: Lazy initialization of S3 resources.
   *
   * @return The S3Client instance
   */
  private S3Client s3() {
    if (s3Client == null) {
      synchronized (this) {
        if (s3Client == null) {
          s3Client =
              S3Client.builder()
                  .region(Region.of(s3Region))
                  .credentialsProvider(DefaultCredentialsProvider.create())
                  .build();
        }
      }
    }
    return s3Client;
  }

  /**
   * Generates a virtual-hosted style public URL for an S3 object.
   *
   * <p>Flow: Constructs the URL string using the bucket name, region, and object key.
   *
   * <p>Features: Standards-compliant S3 URL generation.
   *
   * @param key The S3 object key
   * @return The public URL string
   */
  private String virtualHostedStylePublicUrl(String key) {
    return "https://" + s3Bucket + ".s3." + s3Region + ".amazonaws.com/" + key;
  }

  /**
   * Resolves the file extension from the original filename or content type.
   *
   * <p>Flow: First attempts to extract the extension from the filename. If unsuccessful or
   * invalid, it maps the content type to a known extension. Defaults to .bin.
   *
   * <p>Features: Robust extension detection for consistent file handling.
   *
   * @param file The multipart file
   * @return The resolved extension (e.g., .jpg, .mp4)
   */
  private static String resolveExtension(MultipartFile file) {
    String original = file.getOriginalFilename();
    if (original != null && original.contains(".")) {
      String ext = original.substring(original.lastIndexOf('.')).toLowerCase();
      if (ext.length() <= 10 && ext.matches("\\.[a-z0-9]+")) {
        return ext;
      }
    }
    String ct = file.getContentType();
    if (ct != null) {
      if (ct.contains("jpeg") || ct.contains("jpg")) return ".jpg";
      if (ct.contains("png")) return ".png";
      if (ct.contains("gif")) return ".gif";
      if (ct.contains("webp")) return ".webp";
      if (ct.contains("mp4")) return ".mp4";
      if (ct.contains("webm")) return ".webm";
      if (ct.contains("quicktime")) return ".mov";
    }
    return ".bin";
  }
}
