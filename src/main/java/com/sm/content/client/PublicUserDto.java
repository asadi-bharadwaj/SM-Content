package com.sm.content.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * **Summary:** Data Transfer Object for public user profiles.
 * 
 * **Flow:** This class is used to receive user profile information from the Profile service. 
 * It is marked with `@JsonIgnoreProperties(ignoreUnknown = true)` to ensure compatibility 
 * even if the Profile service adds new fields. It encapsulates basic user info like 
 * username, display name, avatar, and bio.
 * 
 * **Features:** Inter-service data sharing, user profile representation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicUserDto {

  private String id;
  private Long authUserId;
  private String username;
  private String displayName;
  private String avatarUrl;
  private String bio;

  /**
   * **Summary:** Gets the user ID.
   * 
   * **Flow:** Returns the unique identifier for the user.
   * 
   * **Features:** Unique identification.
   * 
   * @return The user ID as a String.
   */
  public String getId() {
    return id;
  }

  /**
   * **Summary:** Sets the user ID.
   * 
   * **Flow:** Assigns a unique identifier to the user.
   * 
   * **Features:** Data initialization.
   * 
   * @param id The user ID to set.
   */
  public void setId(String id) {
    this.id = id;
  }

  public Long getAuthUserId() {
    return authUserId;
  }

  public void setAuthUserId(Long authUserId) {
    this.authUserId = authUserId;
  }

  /**
   * **Summary:** Gets the username.
   * 
   * **Flow:** Returns the unique handle of the user.
   * 
   * **Features:** User identification.
   * 
   * @return The username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * **Summary:** Sets the username.
   * 
   * **Flow:** Assigns the unique handle of the user.
   * 
   * **Features:** User identification.
   * 
   * @param username The username to set.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * **Summary:** Gets the display name.
   * 
   * **Flow:** Returns the name shown to other users.
   * 
   * **Features:** Personalized display.
   * 
   * @return The display name.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * **Summary:** Sets the display name.
   * 
   * **Flow:** Assigns the name shown to other users.
   * 
   * **Features:** Personalized display.
   * 
   * @param displayName The display name to set.
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * **Summary:** Gets the avatar URL.
   * 
   * **Flow:** Returns the link to the user's profile picture.
   * 
   * **Features:** Visual representation.
   * 
   * @return The avatar URL.
   */
  public String getAvatarUrl() {
    return avatarUrl;
  }

  /**
   * **Summary:** Sets the avatar URL.
   * 
   * **Flow:** Assigns the link to the user's profile picture.
   * 
   * **Features:** Visual representation.
   * 
   * @param avatarUrl The avatar URL to set.
   */
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  /**
   * **Summary:** Gets the user bio.
   * 
   * **Flow:** Returns the short biography of the user.
   * 
   * **Features:** User profile details.
   * 
   * @return The bio string.
   */
  public String getBio() {
    return bio;
  }

  /**
   * **Summary:** Sets the user bio.
   * 
   * **Flow:** Assigns the short biography of the user.
   * 
   * **Features:** User profile details.
   * 
   * @param bio The bio string to set.
   */
  public void setBio(String bio) {
    this.bio = bio;
  }
}
