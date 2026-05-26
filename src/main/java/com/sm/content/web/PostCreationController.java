package com.sm.content.web;

import com.sm.content.service.MediaStorageService;
import com.sm.content.service.PostWriteService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;

/**
 * **Summary:** REST Controller for creating new posts with media.
 * 
 * **Flow:** This controller handles multi-part form data requests to create posts. 
 * It validates the uploaded file, stores it using `MediaStorageService`, 
 * and then persists the post metadata using `PostWriteService`.
 * 
 * **Features:** Post creation, media upload handling, file validation.
 */
@RestController
public class PostCreationController {

  private static final Logger log = LoggerFactory.getLogger(PostCreationController.class);

  private final MediaStorageService mediaStorageService;
  private final PostWriteService postWriteService;

  /**
   * **Summary:** Constructs the PostCreationController with necessary services.
   * 
   * **Flow:** Initializes the controller with media storage and post write services via constructor injection.
   * 
   * **Features:** Dependency injection.
   * 
   * @param mediaStorageService Service for storing media files.
   * @param postWriteService Service for persisting post data.
   */
  public PostCreationController(
      MediaStorageService mediaStorageService, PostWriteService postWriteService) {
    this.mediaStorageService = mediaStorageService;
    this.postWriteService = postWriteService;
  }

  /**
   * **Summary:** Creates a new post with an uploaded file.
   * 
   * **Flow:** 
   * 1. Extracts the author ID from the authenticated principal.
   * 2. Validates the uploaded file (size, content type).
   * 3. Determines the base URL for the media link.
   * 4. Stores the file in the file system and gets the public URL.
   * 5. Creates the post record in the database with the media URL and metadata.
   * 6. Returns the created post details as a `PostDto`.
   * 
   * **Features:** Image/Video post creation, automated media type detection.
   * 
   * @param principal The security principal of the author.
   * @param file The multipart file to upload.
   * @param caption Optional text caption for the post.
   * @param visibility Post visibility (default: public).
   * @param tags Optional comma-separated tags.
   * @return A ResponseEntity containing the created PostDto and HTTP 201 status.
   * @throws ResponseStatusException If file storage fails.
   */
  @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<PostDto> createPost(
      Principal principal,
      @RequestPart("file") MultipartFile file,
      @RequestParam(value = "caption", required = false) String caption,
      @RequestParam(value = "visibility", required = false, defaultValue = "public")
          String visibility,
      @RequestParam(value = "tags", required = false) String tags) {

    long authorId = Long.parseLong(principal.getName());
    PostWriteService.validateFile(file);

    String base =
        ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

    String mediaUrl;
    try {
      mediaUrl = mediaStorageService.store(file, authorId, base);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Could not store upload: " + e.getMessage());
    }

    String mediaType = PostWriteService.mediaTypeFromContentType(file.getContentType());
    PostDto body =
        postWriteService.createPost(authorId, mediaUrl, mediaType, caption, visibility, tags, null, null);
    log.info(
        "Post created: id={} authorId={} mediaType={} sizeBytes={} mediaUrl={}",
        body.getId(),
        authorId,
        mediaType,
        file.getSize(),
        mediaUrl);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }
}
