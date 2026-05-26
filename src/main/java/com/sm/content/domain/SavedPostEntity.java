package com.sm.content.domain;

import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;

/**
 * Entity representing a post saved by a user.
 *
 * <p>Flow: Persisted in the 'saved_posts' table. It maps users to the posts they have
 * bookmarked, allowing for personal collection management.
 *
 * <p>Features: Bookmarking and personal content organization.
 */
@Entity
@Table(name = "saved_posts", indexes = {
    @Index(name = "saved_post_user_unique", columnList = "postId, userId", unique = true)
})
public class SavedPostEntity {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long postId;
  private Long userId;
  private Instant createdAt;

  /**
   * Gets the unique ID of the saved post entry.
   * @return The entry ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique ID of the saved post entry.
   * @param id The entry ID
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the ID of the saved post.
   * @return The post ID
   */
  public Long getPostId() {
    return postId;
  }

  /**
   * Sets the ID of the saved post.
   * @param postId The post ID
   */
  public void setPostId(Long postId) {
    this.postId = postId;
  }

  /**
   * Gets the ID of the user who saved the post.
   * @return The user ID
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * Sets the ID of the user who saved the post.
   * @param userId The user ID
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /**
   * Gets the timestamp when the post was saved.
   * @return Creation instant
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the timestamp when the post was saved.
   * @param createdAt Creation instant
   */
  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
