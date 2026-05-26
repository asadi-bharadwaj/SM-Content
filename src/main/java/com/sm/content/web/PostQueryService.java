package com.sm.content.web;

import com.sm.content.client.ProfileServiceClient;
import com.sm.content.client.PublicUserDto;
import com.sm.content.domain.PostEntity;
import com.sm.content.domain.PostRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for querying and transforming post data for presentation.
 *
 * <p>Flow: Interacts with PostRepository to fetch raw post data and ProfileServiceClient to enrich
 * it with author profile information. It handles the mapping of PostEntity to PostDto.
 *
 * <p>Features: Supports listing posts by author and retrieving individual posts by ID, including
 * tag parsing and author enrichment.
 */
@Service
public class PostQueryService {

  private final PostRepository postRepository;
  private final ProfileServiceClient profileClient;

  /**
   * Constructs the PostQueryService.
   *
   * @param postRepository Repository for post data
   * @param profileClient Client for fetching user profiles
   */
  public PostQueryService(PostRepository postRepository, ProfileServiceClient profileClient) {
    this.postRepository = postRepository;
    this.profileClient = profileClient;
  }

  /**
   * Lists all posts authored by a specific user, ordered by creation date descending.
   *
   * <p>Flow: Fetches the public profile of the author via the profile service, then retrieves all
   * posts from the repository. Each post is converted to a DTO enriched with the author profile.
   *
   * <p>Features: Content history display for user profiles.
   *
   * @param authorUserId The ID of the author
   * @return List of enriched PostDto objects
   */
  public List<PostDto> listByAuthor(long authorUserId) {
    PublicUserDto author = profileClient.getPublicProfile(authorUserId);
    List<PostDto> out = new ArrayList<>();
    for (PostEntity p : postRepository.findByAuthorIdOrderByCreatedAtDesc(authorUserId)) {
      out.add(toDto(p, author));
    }
    return out;
  }

  /**
   * Retrieves a single post by its ID.
   *
   * <p>Flow: Attempts to find the post in the repository. If found, it fetches the author's
   * profile and returns an enriched DTO.
   *
   * <p>Features: Detailed post view support.
   *
   * @param postId The ID of the post to retrieve
   * @return Enriched PostDto object
   * @throws ResponseStatusException if the post is not found (404)
   */
  public PostDto getById(long postId) {
    PostEntity p =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    PublicUserDto author = profileClient.getPublicProfile(p.getAuthorId());
    return toDto(p, author);
  }

  /**
   * Converts a PostEntity and author profile into a PostDto.
   *
   * <p>Flow: Maps fields from the entity to the DTO, sets the enriched author object, and parses
   * comma-separated tags into a list.
   *
   * <p>Features: Data transformation for API responses.
   *
   * @param p The post entity
   * @param author The author's profile DTO
   * @return The resulting PostDto
   */
  private PostDto toDto(PostEntity p, PublicUserDto author) {
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
    return dto;
  }

  /**
   * Parses a raw comma-separated string of tags into a list of trimmed strings.
   *
   * <p>Flow: Splits the string by commas, trims each resulting tag, and filters out any empty strings.
   *
   * <p>Features: Support for categorized content searching and display.
   *
   * @param raw The raw tags string
   * @return List of parsed tags
   */
  private static List<String> parseTags(String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
  }
}
