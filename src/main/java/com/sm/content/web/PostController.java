package com.sm.content.web;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling post-related requests.
 *
 * <p>Flow: This controller acts as the entry point for post-related API calls. it delegates
 * business logic to PostQueryService.
 *
 * <p>Features: Provides endpoints for retrieving posts by a specific user.
 */
@RestController
@RequestMapping("/users")
public class PostController {

  private final PostQueryService postQueryService;

  /**
   * Constructs the PostController.
   *
   * @param postQueryService Service for querying posts
   */
  public PostController(PostQueryService postQueryService) {
    this.postQueryService = postQueryService;
  }

  /**
   * Retrieves all posts authored by a specific user.
   *
   * <p>Flow: Delegates the retrieval to PostQueryService, which fetches the author's profile and
   * then their posts from the repository.
   *
   * <p>Features: Support for user profile pages displaying their content history.
   *
   * @param userId The ID of the user whose posts are to be retrieved
   * @return List of PostDto representing the user's posts
   */
  @GetMapping("/{userId}/posts")
  public List<PostDto> posts(@PathVariable Long userId) {
    return postQueryService.listByAuthor(userId);
  }
}
