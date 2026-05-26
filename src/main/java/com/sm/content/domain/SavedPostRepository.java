package com.sm.content.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing SavedPostEntity persistence.
 *
 * <p>Flow: Provides standard CRUD operations and custom queries for bookmarks.
 *
 * <p>Features: Support for user bookmark collections and duplicate prevention.
 */
@Repository
public interface SavedPostRepository extends JpaRepository<SavedPostEntity, Long> {
  
  /**
   * Finds a saved post entry by post ID and user ID.
   *
   * <p>Flow: Executes a selective query to find a specific user's bookmark for a post.
   *
   * @param postId The ID of the post
   * @param userId The ID of the user
   * @return Optional containing the saved post entry if it exists
   */
  Optional<SavedPostEntity> findByPostIdAndUserId(Long postId, Long userId);

  /**
   * Lists all posts saved by a user, ordered by most recent first.
   *
   * <p>Flow: Queries the 'saved_posts' table for all entries matching the user ID, sorted by creation date.
   *
   * @param userId The ID of the user
   * @return List of saved post entities
   */
  List<SavedPostEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
