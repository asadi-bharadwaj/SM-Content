package com.sm.content.service;

import com.sm.content.client.ProfileServiceClient;
import com.sm.content.client.PublicUserDto;
import com.sm.content.domain.PostEntity;
import com.sm.content.domain.PostRepository;
import com.sm.content.web.PostDto;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for creating and persisting new posts.
 *
 * <p>Flow: Orchestrates the post creation process by validating input, calculating next IDs,
 * normalizing metadata (visibility, tags), and persisting the PostEntity. It also enriches the
 * result with author information from the profile service.
 *
 * <p>Features: Secure post creation, metadata normalization, and automatic ID generation.
 */
@Service
public class PostWriteService {

  private final PostRepository postRepository;
  private final ProfileServiceClient profileServiceClient;

  /**
   * Constructs the PostWriteService.
   *
   * @param postRepository Repository for post data
   * @param profileServiceClient Client for fetching author profile
   */
  public PostWriteService(
      PostRepository postRepository, ProfileServiceClient profileServiceClient) {
    this.postRepository = postRepository;
    this.profileServiceClient = profileServiceClient;
  }

  /**
   * Creates a new post and returns its DTO.
   *
   * <p>Flow: Normalizes visibility and tags, generates a new ID, creates a PostEntity, and saves
   * it to the repository. It then fetches the author's profile to return a fully populated PostDto.
   *
   * <p>Features: Core content creation functionality.
   *
   * @param authorId ID of the user creating the post
   * @param mediaUrl Public URL of the uploaded media
   * @param mediaType Type of media (image/video)
   * @param caption Optional text caption
   * @param visibility Access level (public, subscribers, tier)
   * @param tagsRaw Raw tag string
   * @return The created PostDto
   */
  public PostDto createPost(
      long authorId,
      String mediaUrl,
      String mediaType,
      String caption,
      String visibility,
      String tagsRaw,
      String location,
      String musicTrack) {

    String vis = normalizeVisibility(visibility);
    PostEntity p = new PostEntity();
    p.setId(nextId());
    p.setAuthorId(authorId);
    p.setMediaUrl(mediaUrl);
    p.setMediaType(mediaType);
    p.setCaption(caption != null ? caption : "");
    p.setCreatedAt(Instant.now());
    p.setVisibility(vis);
    p.setTags(normalizeTags(tagsRaw));
    p.setLocation(location);
    p.setMusicTrack(musicTrack);

    postRepository.save(p);

    PublicUserDto author = profileServiceClient.getPublicProfile(authorId);
    return toDto(p, author);
  }

  /**
   * Updates an existing post's caption and location.
   *
   * @param postId ID of the post
   * @param authorId ID of the user requesting update
   * @param caption New caption
   * @param location New location
   * @return Updated PostDto
   */
  public PostDto updatePost(long postId, long authorId, String caption, String location) {
    PostEntity p = postRepository.findById(postId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

    if (p.getAuthorId() != authorId) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to edit this post");
    }

    p.setCaption(caption != null ? caption : "");
    p.setLocation(location);
    postRepository.save(p);

    PublicUserDto author = profileServiceClient.getPublicProfile(authorId);
    return toDto(p, author);
  }

  /**
   * Deletes an existing post.
   *
   * @param postId ID of the post
   * @param authorId ID of the user requesting deletion
   */
  public void deletePost(long postId, long authorId) {
    PostEntity p = postRepository.findById(postId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

    if (p.getAuthorId() != authorId) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this post");
    }

    postRepository.delete(p);
  }

  /**
   * Generates the next sequential post ID.
   *
   * <p>Flow: Finds the highest current ID in the repository and increments it by one. Defaults to 1.
   *
   * <p>Features: Custom ID generation logic (alternative to DB-native auto-increment).
   *
   * @return The next available ID
   */
  private long nextId() {
    return postRepository.findFirstByOrderByIdDesc().map(PostEntity::getId).map(id -> id + 1).orElse(1L);
  }

  /**
   * Validates and normalizes the visibility string.
   *
   * <p>Flow: Trims and lowercases the input. Matches against allowed values ('public',
   * 'subscribers', 'tier'). Defaults to 'public' if invalid or missing.
   *
   * <p>Features: Access control enforcement for content.
   *
   * @param v The raw visibility string
   * @return Normalized visibility string
   */
  private static String normalizeVisibility(String v) {
    if (v == null || v.isBlank()) {
      return "public";
    }
    String s = v.trim().toLowerCase();
    if ("public".equals(s) || "subscribers".equals(s) || "tier".equals(s)) {
      return s;
    }
    return "public";
  }

  /**
   * Trims the raw tags string.
   *
   * @param raw Raw tags string
   * @return Trimmed tags string
   */
  private static String normalizeTags(String raw) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    return raw.trim();
  }

  /**
   * Converts PostEntity and author profile to PostDto.
   *
   * @param p Post entity
   * @param author Author profile DTO
   * @return Mapped PostDto
   */
  private static PostDto toDto(PostEntity p, PublicUserDto author) {
    PostDto dto = new PostDto();
    dto.setId(String.valueOf(p.getId()));
    dto.setAuthorId(String.valueOf(p.getAuthorId()));
    dto.setAuthor(author);
    dto.setMediaUrl(p.getMediaUrl());
    dto.setMediaType(p.getMediaType());
    dto.setCaption(p.getCaption() != null ? p.getCaption() : "");
    dto.setCreatedAt(p.getCreatedAt());
    dto.setVisibility(p.getVisibility());
    dto.setTags(parseTags(p.getTags()));
    dto.setLocation(p.getLocation());
    dto.setMusicTrack(p.getMusicTrack());
    return dto;
  }

  /**
   * Parses tags from a comma-separated string.
   *
   * @param raw Raw tags string
   * @return List of tags
   */
  private static List<String> parseTags(String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
  }

  /**
   * Determines media type category from MIME content type.
   *
   * <p>Flow: Checks if the content type starts with 'video/'. If so, returns 'video', otherwise 'image'.
   *
   * <p>Features: Content type classification.
   *
   * @param contentType The MIME type (e.g., image/jpeg)
   * @return 'video' or 'image'
   */
  public static String mediaTypeFromContentType(String contentType) {
    if (contentType != null && contentType.toLowerCase().startsWith("video/")) {
      return "video";
    }
    return "image";
  }

  /**
   * Validates that an uploaded file is a valid image or video.
   *
   * <p>Flow: Checks for empty file and verifies that the MIME type is supported (image/*,
   * video/*, or octet-stream).
   *
   * <p>Features: Upload security and validation.
   *
   * @param file The multipart file to validate
   * @throws ResponseStatusException 400 if empty, 415 if unsupported type
   */
  public static void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A media file is required");
    }
    String ct = file.getContentType();
    if (ct == null) {
      return;
    }
    String lower = ct.toLowerCase();
    boolean ok =
        lower.startsWith("image/")
            || lower.startsWith("video/")
            || lower.equals("application/octet-stream");
    if (!ok) {
      throw new ResponseStatusException(
          HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only image or video uploads are allowed");
    }
  }
}
