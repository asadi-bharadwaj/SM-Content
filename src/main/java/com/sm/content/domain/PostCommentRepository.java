package com.sm.content.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing PostCommentEntity persistence.
 *
 * <p>Flow: Handles database operations for post comments, including retrieval and counting.
 *
 * <p>Features: Support for comment threads and engagement metrics.
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {

  /**
   * Retrieves all comments for a specific post, ordered by creation date ascending.
   *
   * @param postId The ID of the post
   * @return List of comment entities
   */
  List<PostCommentEntity> findByPostIdOrderByCreatedAtAsc(Long postId);

  /**
   * Counts the total number of comments for a specific post.
   *
   * @param postId The ID of the post
   * @return The number of comments
   */
  long countByPostId(Long postId);
}
