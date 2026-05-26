package com.sm.content.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for local file storage management.
 *
 * <p>Flow: Handles saving multipart files to a configured local directory. It generates unique
 * names for each file to avoid conflicts.
 *
 * <p>Features: Local filesystem storage, unique filename generation, and extension resolution.
 */
@Service
public class FileStorageService {

  @Value("${content.upload.directory:./data/uploads}")
  private String uploadDirectory;

  /**
   * Saves the file to the local directory and returns the generated filename.
   *
   * <p>Flow: Ensures the target directory exists, resolves the file extension, generates a UUID-based
   * name, and transfers the file content to the target path.
   *
   * <p>Features: Secure local storage with randomized filenames.
   *
   * @param file The multipart file to save
   * @return The stored file name (under /media/)
   * @throws IOException If an error occurs during file transfer
   */
  public String store(MultipartFile file) throws IOException {
    Path dir = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    Files.createDirectories(dir);

    String ext = resolveExtension(file);
    String name = UUID.randomUUID() + ext;
    Path target = dir.resolve(name);
    file.transferTo(target);
    return name;
  }

  /**
   * Resolves the file extension from the original filename or content type.
   *
   * <p>Flow: Attempts to parse the extension from the filename. If missing or invalid, it maps
   * common image and video MIME types to their respective extensions.
   *
   * <p>Features: Consistent file extension mapping for media assets.
   *
   * @param file The multipart file
   * @return The resolved extension
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
