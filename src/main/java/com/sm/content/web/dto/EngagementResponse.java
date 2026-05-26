package com.sm.content.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * **Summary:** Data Transfer Object for post engagement metrics.
 * 
 * **Flow:** This class consolidates engagement data for a post, such as like and comment counts, 
 * whether the current user has liked or saved the post, and a list of recent comments. It 
 * provides a comprehensive overview of post activity for the frontend.
 * 
 * **Features:** Engagement summary, social proof metrics, user-specific interaction status.
 */
public class EngagementResponse {

  private Long postId;
  private long likeCount;
  private long commentCount;
  private boolean liked;
  private boolean saved;
  private List<CommentResponse> comments = new ArrayList<>();

  /**
   * **Summary:** Gets the post ID.
   * 
   * **Flow:** Returns the ID of the post these engagement metrics refer to.
   * 
   * **Features:** Relational context.
   * 
   * @return The post ID.
   */
  public Long getPostId() {
    return postId;
  }

  /**
   * **Summary:** Sets the post ID.
   * 
   * **Flow:** Assigns the ID of the post these engagement metrics refer to.
   * 
   * **Features:** Relational context.
   * 
   * @param postId The post ID to set.
   */
  public void setPostId(Long postId) {
    this.postId = postId;
  }

  /**
   * **Summary:** Gets the number of likes.
   * 
   * **Flow:** Returns the total count of likes for the post.
   * 
   * **Features:** Social proof, popularity tracking.
   * 
   * @return The like count.
   */
  public long getLikeCount() {
    return likeCount;
  }

  /**
   * **Summary:** Sets the number of likes.
   * 
   * **Flow:** Assigns the total count of likes for the post.
   * 
   * **Features:** Metric aggregation.
   * 
   * @param likeCount The like count to set.
   */
  public void setLikeCount(long likeCount) {
    this.likeCount = likeCount;
  }

  /**
   * **Summary:** Gets the number of comments.
   * 
   * **Flow:** Returns the total count of comments for the post.
   * 
   * **Features:** Engagement tracking.
   * 
   * @return The comment count.
   */
  public long getCommentCount() {
    return commentCount;
  }

  /**
   * **Summary:** Sets the number of comments.
   * 
   * **Flow:** Assigns the total count of comments for the post.
   * 
   * **Features:** Metric aggregation.
   * 
   * @param commentCount The comment count to set.
   */
  public void setCommentCount(long commentCount) {
    this.commentCount = commentCount;
  }

  /**
   * **Summary:** Checks if the post is liked by the current user.
   * 
   * **Flow:** Returns true if the user in context has liked this post.
   * 
   * **Features:** User-specific state.
   * 
   * @return true if liked, false otherwise.
   */
  public boolean isLiked() {
    return liked;
  }

  /**
   * **Summary:** Sets the liked status for the current user.
   * 
   * **Flow:** Assigns whether the user in context has liked this post.
   * 
   * **Features:** Personalized response.
   * 
   * @param liked The liked status to set.
   */
  public void setLiked(boolean liked) {
    this.liked = liked;
  }

  /**
   * **Summary:** Checks if the post is saved by the current user.
   * 
   * **Flow:** Returns true if the user in context has saved this post.
   * 
   * **Features:** User-specific state.
   * 
   * @return true if saved, false otherwise.
   */
  public boolean isSaved() {
    return saved;
  }

  /**
   * **Summary:** Sets the saved status for the current user.
   * 
   * **Flow:** Assigns whether the user in context has saved this post.
   * 
   * **Features:** Personalized response.
   * 
   * @param saved The saved status to set.
   */
  public void setSaved(boolean saved) {
    this.saved = saved;
  }

  /**
   * **Summary:** Gets the list of comments.
   * 
   * **Flow:** Returns a list of `CommentResponse` objects associated with the post.
   * 
   * **Features:** Content delivery.
   * 
   * @return List of comments.
   */
  public List<CommentResponse> getComments() {
    return comments;
  }

  /**
   * **Summary:** Sets the list of comments.
   * 
   * **Flow:** Assigns a list of `CommentResponse` objects associated with the post.
   * 
   * **Features:** Content delivery.
   * 
   * @param comments List of comments to set.
   */
  public void setComments(List<CommentResponse> comments) {
    this.comments = comments;
  }
}
