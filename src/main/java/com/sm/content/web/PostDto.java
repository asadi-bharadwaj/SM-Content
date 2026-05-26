package com.sm.content.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sm.content.client.PublicUserDto;
import java.time.Instant;
import java.util.List;

/**
 * **Summary:** Data Transfer Object for post details.
 * 
 * **Flow:** This class is used to transfer post information between the server and the client. 
 * It includes post metadata, media details, and enriched author information. It uses 
 * `@JsonInclude(JsonInclude.Include.NON_NULL)` to omit null fields from the JSON response.
 * 
 * **Features:** Post representation, author enrichment, data transfer optimization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {

  private String id;
  private String authorId;
  private PublicUserDto author;
  private String mediaUrl;
  private String mediaType;
  private String caption;
  private Instant createdAt;
  private List<String> tags;
  private String visibility;
  private String location;
  private String musicTrack;

  /**
   * **Summary:** Gets the post ID.
   * 
   * **Flow:** Returns the unique identifier for the post.
   * 
   * **Features:** Unique identification.
   * 
   * @return The post ID.
   */
  public String getId() {
    return id;
  }

  /**
   * **Summary:** Sets the post ID.
   * 
   * **Flow:** Assigns a unique identifier to the post.
   * 
   * **Features:** Data initialization.
   * 
   * @param id The post ID to set.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * **Summary:** Gets the author ID.
   * 
   * **Flow:** Returns the ID of the user who created the post.
   * 
   * **Features:** Author identification.
   * 
   * @return The author ID.
   */
  public String getAuthorId() {
    return authorId;
  }

  /**
   * **Summary:** Sets the author ID.
   * 
   * **Flow:** Assigns the ID of the user who created the post.
   * 
   * **Features:** Author assignment.
   * 
   * @param authorId The author ID to set.
   */
  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  /**
   * **Summary:** Gets the enriched author profile.
   * 
   * **Flow:** Returns the detailed public profile of the author.
   * 
   * **Features:** User information enrichment.
   * 
   * @return The PublicUserDto of the author.
   */
  public PublicUserDto getAuthor() {
    return author;
  }

  /**
   * **Summary:** Sets the enriched author profile.
   * 
   * **Flow:** Assigns the detailed public profile of the author.
   * 
   * **Features:** Response enrichment.
   * 
   * @param author The PublicUserDto to set.
   */
  public void setAuthor(PublicUserDto author) {
    this.author = author;
  }

  /**
   * **Summary:** Gets the media URL.
   * 
   * **Flow:** Returns the link to the post's media content (image/video).
   * 
   * **Features:** Content delivery.
   * 
   * @return The media URL.
   */
  public String getMediaUrl() {
    return mediaUrl;
  }

  /**
   * **Summary:** Sets the media URL.
   * 
   * **Flow:** Assigns the link to the post's media content.
   * 
   * **Features:** Content assignment.
   * 
   * @param mediaUrl The media URL to set.
   */
  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }

  /**
   * **Summary:** Gets the media type.
   * 
   * **Flow:** Returns the type of media (e.g., "image", "video").
   * 
   * **Features:** Content categorization.
   * 
   * @return The media type.
   */
  public String getMediaType() {
    return mediaType;
  }

  /**
   * **Summary:** Sets the media type.
   * 
   * **Flow:** Assigns the type of media.
   * 
   * **Features:** Content categorization.
   * 
   * @param mediaType The media type to set.
   */
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  /**
   * **Summary:** Gets the post caption.
   * 
   * **Flow:** Returns the text description accompanying the post.
   * 
   * **Features:** User-generated content delivery.
   * 
   * @return The caption text.
   */
  public String getCaption() {
    return caption;
  }

  /**
   * **Summary:** Sets the post caption.
   * 
   * **Flow:** Assigns the text description accompanying the post.
   * 
   * **Features:** Content assignment.
   * 
   * @param caption The caption text to set.
   */
  public void setCaption(String caption) {
    this.caption = caption;
  }

  /**
   * **Summary:** Gets the creation timestamp.
   * 
   * **Flow:** Returns when the post was created.
   * 
   * **Features:** Temporal tracking.
   * 
   * @return The creation timestamp.
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * **Summary:** Sets the creation timestamp.
   * 
   * **Flow:** Assigns when the post was created.
   * 
   * **Features:** Temporal metadata.
   * 
   * @param createdAt The timestamp to set.
   */
  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * **Summary:** Gets the post tags.
   * 
   * **Flow:** Returns a list of tags associated with the post.
   * 
   * **Features:** Content indexing, discovery.
   * 
   * @return List of tags.
   */
  public List<String> getTags() {
    return tags;
  }

  /**
   * **Summary:** Sets the post tags.
   * 
   * **Flow:** Assigns a list of tags associated with the post.
   * 
   * **Features:** Content indexing.
   * 
   * @param tags List of tags to set.
   */
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   * **Summary:** Gets the post visibility.
   * 
   * **Flow:** Returns the visibility level (e.g., "public", "private").
   * 
   * **Features:** Access control, privacy.
   * 
   * @return The visibility status.
   */
  public String getVisibility() {
    return visibility;
  }

  /**
   * **Summary:** Sets the post visibility.
   * 
   * **Flow:** Assigns the visibility level.
   * 
   * **Features:** Access control.
   * 
   * @param visibility The visibility status to set.
   */
  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  /**
   * **Summary:** Gets the location.
   * @return The location.
   */
  public String getLocation() {
    return location;
  }

  /**
   * **Summary:** Sets the location.
   * @param location The location to set.
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * **Summary:** Gets the music track.
   * @return The music track.
   */
  public String getMusicTrack() {
    return musicTrack;
  }

  /**
   * **Summary:** Sets the music track.
   * @param musicTrack The music track to set.
   */
  public void setMusicTrack(String musicTrack) {
    this.musicTrack = musicTrack;
  }
}
