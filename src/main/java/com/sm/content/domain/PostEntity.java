package com.sm.content.domain;

import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Entity representing a main content post.
 *
 * <p>Flow: This is the primary entity for the content service, stored in the 'posts' table. It
 * holds metadata for media files, author identification, and visibility settings.
 *
 * <p>Features: Support for images/videos, captions, visibility levels (public/subscribers/tier),
 * and tagging.
 */
@Entity
@Table(name = "posts")
public class PostEntity {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long authorId;
  private String mediaUrl;
  private String mediaType;
  
  @Column(length = 2000)
  private String caption;
  
  private Instant createdAt;
  private String visibility;
  private String tags;
  private String location;
  private String musicTrack;

  /**
   * Gets the unique ID of the post.
   * @return The post ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique ID of the post.
   * @param id The post ID
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the ID of the authoring user.
   * @return The author ID
   */
  public Long getAuthorId() {
    return authorId;
  }

  /**
   * Sets the ID of the authoring user.
   * @param authorId The author ID
   */
  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  /**
   * Gets the URL of the stored media.
   * @return The media URL
   */
  public String getMediaUrl() {
    return mediaUrl;
  }

  /**
   * Sets the URL of the stored media.
   * @param mediaUrl The media URL
   */
  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }

  /**
   * Gets the type of media (e.g., image, video).
   * @return The media type
   */
  public String getMediaType() {
    return mediaType;
  }

  /**
   * Sets the type of media.
   * @param mediaType The media type
   */
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  /**
   * Gets the post caption.
   * @return The caption text
   */
  public String getCaption() {
    return caption;
  }

  /**
   * Sets the post caption.
   * @param caption The caption text
   */
  public void setCaption(String caption) {
    this.caption = caption;
  }

  /**
   * Gets the creation timestamp.
   * @return Creation instant
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the creation timestamp.
   * @param createdAt Creation instant
   */
  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Gets the post visibility (public, subscribers, tier).
   * @return Visibility level
   */
  public String getVisibility() {
    return visibility;
  }

  /**
   * Sets the post visibility.
   * @param visibility Visibility level
   */
  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  /**
   * Gets the raw comma-separated tags string.
   * @return Raw tags
   */
  public String getTags() {
    return tags;
  }

  /**
   * Sets the raw comma-separated tags string.
   * @param tags Raw tags
   */
  public void setTags(String tags) {
    this.tags = tags;
  }

  /**
   * Gets the location string.
   * @return Location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets the location string.
   * @param location Location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Gets the music track string.
   * @return Music track
   */
  public String getMusicTrack() {
    return musicTrack;
  }

  /**
   * Sets the music track string.
   * @param musicTrack Music track
   */
  public void setMusicTrack(String musicTrack) {
    this.musicTrack = musicTrack;
  }
}
