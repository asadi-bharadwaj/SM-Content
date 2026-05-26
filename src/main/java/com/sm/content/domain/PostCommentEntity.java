package com.sm.content.domain;

import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Index;

/**
 * Entity representing a comment on a post.
 *
 * <p>Flow: This entity is persisted in the 'post_comments' table and is used to store user
 * feedback and discussions related to specific posts.
 *
 * <p>Features: Support for social interaction via text-based comments.
 */
@Entity
@Table(name = "post_comments", indexes = {
    @Index(name = "idx_post_id", columnList = "postId")
})
public class PostCommentEntity {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long postId;
  private Long userId;
  
  @Column(length = 2000)
  private String text;
  
  private Instant createdAt;

  /**
   * Gets the unique ID of the comment.
   * @return The comment ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique ID of the comment.
   * @param id The comment ID
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the ID of the post this comment belongs to.
   * @return The post ID
   */
  public Long getPostId() {
    return postId;
  }

  /**
   * Sets the ID of the post this comment belongs to.
   * @param postId The post ID
   */
  public void setPostId(Long postId) {
    this.postId = postId;
  }

  /**
   * Gets the ID of the user who created the comment.
   * @return The user ID
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * Sets the ID of the user who created the comment.
   * @param userId The user ID
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /**
   * Gets the text content of the comment.
   * @return The comment text
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text content of the comment.
   * @param text The comment text
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Gets the timestamp when the comment was created.
   * @return The creation timestamp
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the timestamp when the comment was created.
   * @param createdAt The creation timestamp
   */
  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
