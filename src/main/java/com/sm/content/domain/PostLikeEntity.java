package com.sm.content.domain;

import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;

/**
 * Entity representing a 'Like' on a post.
 *
 * <p>Flow: Persisted in the 'post_likes' table with a unique constraint on (postId, userId) to
 * ensure a user can only like a post once.
 *
 * <p>Features: Support for post popularity tracking and user engagement.
 */
@Entity
@Table(name = "post_likes", indexes = {
    @Index(name = "post_user_unique", columnList = "postId, userId", unique = true)
})
public class PostLikeEntity {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long postId;
  private Long userId;
  private Instant createdAt;

  /**
   * Gets the unique ID of the like entry.
   * @return The like ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique ID of the like entry.
   * @param id The like ID
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the ID of the post that was liked.
   * @return The post ID
   */
  public Long getPostId() {
    return postId;
  }

  /**
   * Sets the ID of the post that was liked.
   * @param postId The post ID
   */
  public void setPostId(Long postId) {
    this.postId = postId;
  }

  /**
   * Gets the ID of the user who liked the post.
   * @return The user ID
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * Sets the ID of the user who liked the post.
   * @param userId The user ID
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /**
   * Gets the timestamp when the like was created.
   * @return The creation timestamp
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the timestamp when the like was created.
   * @param createdAt The creation timestamp
   */
  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
