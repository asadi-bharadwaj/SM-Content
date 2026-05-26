package com.sm.content.web.dto;

import com.sm.content.client.PublicUserDto;
import java.util.ArrayList;
import java.util.List;

/**
 * **Summary:** Data Transfer Object for post likers.
 * 
 * **Flow:** This class holds a list of user IDs and enriched user profiles for everyone 
 * who liked a specific post. It allows the frontend to display both raw IDs and detailed 
 * user information (names, avatars) for likers.
 * 
 * **Features:** Post engagement visibility, social interaction tracking.
 */
public class LikersResponse {

  private Long postId;
  private List<Long> userIds = new ArrayList<>();
  private List<PublicUserDto> users = new ArrayList<>();

  /**
   * **Summary:** Gets the post ID.
   * 
   * **Flow:** Returns the ID of the post whose likers are listed.
   * 
   * **Features:** Contextual identification.
   * 
   * @return The post ID.
   */
  public Long getPostId() {
    return postId;
  }

  /**
   * **Summary:** Sets the post ID.
   * 
   * **Flow:** Assigns the ID of the post whose likers are listed.
   * 
   * **Features:** Contextual assignment.
   * 
   * @param postId The post ID to set.
   */
  public void setPostId(Long postId) {
    this.postId = postId;
  }

  /**
   * **Summary:** Gets the list of liker user IDs.
   * 
   * **Flow:** Returns a list of raw numerical user IDs for all likers.
   * 
   * **Features:** Raw data access.
   * 
   * @return List of user IDs.
   */
  public List<Long> getUserIds() {
    return userIds;
  }

  /**
   * **Summary:** Sets the list of liker user IDs.
   * 
   * **Flow:** Assigns a list of raw numerical user IDs for all likers.
   * 
   * **Features:** Raw data population.
   * 
   * @param userIds List of user IDs to set.
   */
  public void setUserIds(List<Long> userIds) {
    this.userIds = userIds;
  }

  /**
   * **Summary:** Gets the list of enriched user profiles.
   * 
   * **Flow:** Returns a list of `PublicUserDto` objects containing detailed info for likers.
   * 
   * **Features:** User profile enrichment.
   * 
   * @return List of PublicUserDto objects.
   */
  public List<PublicUserDto> getUsers() {
    return users;
  }

  /**
   * **Summary:** Sets the list of enriched user profiles.
   * 
   * **Flow:** Assigns a list of `PublicUserDto` objects containing detailed info for likers.
   * 
   * **Features:** Response enrichment.
   * 
   * @param users List of PublicUserDto objects to set.
   */
  public void setUsers(List<PublicUserDto> users) {
    this.users = users;
  }
}
