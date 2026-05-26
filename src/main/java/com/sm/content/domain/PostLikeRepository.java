package com.sm.content.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing PostLikeEntity persistence.
 *
 * <p>Flow: Handles database operations for post likes, including searching by post/user pairs and
 * counting total likes.
 *
 * <p>Features: Support for post popularity and personalized user engagement status.
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

  /**
   * Finds a specific like entry for a post by a user.
   *
   * @param postId The ID of the post
   * @param userId The ID of the user
   * @return Optional containing the like entity if it exists
   */
  Optional<PostLikeEntity> findByPostIdAndUserId(Long postId, Long userId);

  /**
   * Lists all like entries for a specific post.
   *
   * @param postId The ID of the post
   * @return List of like entities
   */
  List<PostLikeEntity> findByPostId(Long postId);

  /**
   * Counts the total number of likes for a specific post.
   *
   * @param postId The ID of the post
   * @return The number of likes
   */
  long countByPostId(Long postId);
}
