package com.sm.content.web.dto;

import com.sm.content.client.PublicUserDto;
import java.time.Instant;

/**
 * **Summary:** Data Transfer Object for comment details.
 * 
 * **Flow:** This class is used to encapsulate comment data, including the author's public profile, 
 * to be sent as a response to the client. It bridges the gap between the internal entity 
 * representation and the external API format.
 * 
 * **Features:** Comment representation, user profile enrichment.
 */
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private PublicUserDto user;
    private String text;
    private Instant createdAt;

    /**
     * **Summary:** Gets the comment ID.
     * 
     * **Flow:** Returns the unique identifier for the comment.
     * 
     * **Features:** Unique identification.
     * 
     * @return The comment ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * **Summary:** Sets the comment ID.
     * 
     * **Flow:** Assigns a unique identifier to the comment.
     * 
     * **Features:** Data initialization.
     * 
     * @param id The comment ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * **Summary:** Gets the associated post ID.
     * 
     * **Flow:** Returns the ID of the post this comment belongs to.
     * 
     * **Features:** Relational mapping.
     * 
     * @return The post ID.
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * **Summary:** Sets the associated post ID.
     * 
     * **Flow:** Assigns the ID of the post this comment belongs to.
     * 
     * **Features:** Relational linking.
     * 
     * @param postId The post ID to set.
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    /**
     * **Summary:** Gets the user ID of the commenter.
     * 
     * **Flow:** Returns the raw ID of the user who created the comment.
     * 
     * **Features:** Author identification.
     * 
     * @return The user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * **Summary:** Sets the user ID of the commenter.
     * 
     * **Flow:** Assigns the raw ID of the user who created the comment.
     * 
     * **Features:** Author assignment.
     * 
     * @param userId The user ID to set.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * **Summary:** Gets the enriched user profile.
     * 
     * **Flow:** Returns the detailed public profile of the commenter.
     * 
     * **Features:** User information enrichment.
     * 
     * @return The PublicUserDto of the author.
     */
    public PublicUserDto getUser() {
        return user;
    }

    public void setUser(PublicUserDto user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
