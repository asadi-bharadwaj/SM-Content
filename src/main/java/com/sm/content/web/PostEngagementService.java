package com.sm.content.web;

import com.sm.content.domain.PostCommentEntity;
import com.sm.content.domain.PostCommentRepository;
import com.sm.content.domain.PostEntity;
import com.sm.content.domain.PostLikeEntity;
import com.sm.content.domain.PostLikeRepository;
import com.sm.content.domain.PostRepository;
import com.sm.content.domain.SavedPostEntity;
import com.sm.content.domain.SavedPostRepository;
import com.sm.content.client.ProfileServiceClient;
import com.sm.content.client.PublicUserDto;
import com.sm.content.web.dto.CommentResponse;
import com.sm.content.web.dto.EngagementResponse;
import com.sm.content.web.dto.LikersResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing user engagement with posts, including likes, comments, and saves.
 *
 * <p>Flow: This service interacts with multiple repositories (PostRepository, PostLikeRepository,
 * PostCommentRepository, SavedPostRepository) to retrieve and persist engagement data. It
 * aggregates counts and user-specific status for the web layer.
 *
 * <p>Features: Supports post like/unlike, commenting, saving/unsaving posts, and retrieving
 * engagement metrics for a post.
 */
@Service
@Slf4j
public class PostEngagementService {

  private final PostRepository postRepository;
  private final PostLikeRepository likeRepository;
  private final PostCommentRepository commentRepository;
  private final SavedPostRepository savedPostRepository;
  private final ProfileServiceClient profileClient;
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${internal.secret:defaultSecret}")
  private String internalSecret;

  /**
   * Constructs the PostEngagementService.
   *
   * @param postRepository Repository for post data
   * @param likeRepository Repository for post likes
   * @param commentRepository Repository for post comments
   * @param savedPostRepository Repository for saved posts
   */
  public PostEngagementService(
      PostRepository postRepository,
      PostLikeRepository likeRepository,
      PostCommentRepository commentRepository,
      SavedPostRepository savedPostRepository,
      ProfileServiceClient profileClient) {
    this.postRepository = postRepository;
    this.likeRepository = likeRepository;
    this.commentRepository = commentRepository;
    this.savedPostRepository = savedPostRepository;
    this.profileClient = profileClient;
  }

  /**
   * Retrieves engagement metrics for a specific post.
   *
   * <p>Flow: Queries like and comment repositories for total counts. If a viewerId is provided,
   * it also checks if that user has liked or saved the post.
   *
   * <p>Features: Provides a consolidated view of post popularity and user-specific engagement state.
   *
   * @param postId The ID of the post
   * @param viewerId The ID of the user viewing the post (optional)
   * @return EngagementResponse containing counts and status
   */
  public EngagementResponse getEngagement(long postId, Long viewerId) {
    long likeCount = likeRepository.countByPostId(postId);
    long commentCount = commentRepository.countByPostId(postId);

    boolean liked = false;
    boolean saved = false;

    if (viewerId != null) {
      liked = likeRepository.findByPostIdAndUserId(postId, viewerId).isPresent();
      saved = savedPostRepository.findByPostIdAndUserId(postId, viewerId).isPresent();
    }

    EngagementResponse res = new EngagementResponse();
    res.setPostId(postId);
    res.setLikeCount(likeCount);
    res.setCommentCount(commentCount);
    res.setLiked(liked);
    res.setSaved(saved);
    return res;
  }

  /**
   * Lists users who have liked a specific post.
   *
   * <p>Flow: Fetches like entities for the post and extracts the user IDs, limited by the specified amount.
   *
   * <p>Features: Allows displaying a sample of users who interacted with the post.
   *
   * @param postId The ID of the post
   * @param limit Maximum number of user IDs to return
   * @return LikersResponse containing the list of user IDs
   */
  public LikersResponse listLikers(long postId, int limit) {
    List<Long> userIds = likeRepository.findByPostId(postId).stream()
        .map(PostLikeEntity::getUserId)
        .limit(limit)
        .collect(Collectors.toList());

    List<PublicUserDto> users = userIds.stream()
        .map(profileClient::getPublicProfile)
        .collect(Collectors.toList());

    LikersResponse res = new LikersResponse();
    res.setPostId(postId);
    res.setUserIds(userIds);
    res.setUsers(users);
    return res;
  }

  /**
   * Adds a like to a post by a user.
   *
   * <p>Flow: Checks if a like already exists for the user/post combination. If not, creates and
   * saves a new PostLikeEntity.
   *
   * <p>Features: Implements the 'Like' functionality for posts.
   *
   * @param postId The ID of the post to like
   * @param userId The ID of the user liking the post
   */
  @Transactional
  public void like(long postId, long userId) {
    if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
      return;
    }
    PostLikeEntity entity = new PostLikeEntity();
    entity.setPostId(postId);
    entity.setUserId(userId);
    entity.setCreatedAt(Instant.now());
    likeRepository.save(entity);

    // Trigger notification
    postRepository.findById(postId).ifPresent(post -> {
        if (!post.getAuthorId().equals(userId)) {
            PublicUserDto liker = profileClient.getPublicProfile(userId);
            String name = (liker != null && liker.getUsername() != null) ? liker.getUsername() : "Someone";
            sendNotification(post.getAuthorId(), "PUSH", "New Like", name + " liked your post!");
        }
    });
  }

  /**
   * Removes a like from a post by a user.
   *
   * <p>Flow: Searches for an existing like and deletes it if found.
   *
   * <p>Features: Implements the 'Unlike' functionality for posts.
   *
   * @param postId The ID of the post to unlike
   * @param userId The ID of the user unliking the post
   */
  @Transactional
  public void unlike(long postId, long userId) {
    likeRepository.findByPostIdAndUserId(postId, userId)
        .ifPresent(likeRepository::delete);
  }

  /**
   * Adds a comment to a post.
   *
   * <p>Flow: Creates a new PostCommentEntity with the provided text and metadata, saves it to the
   * database, and returns a DTO representation.
   *
   * <p>Features: Enables user discussion and feedback on posts.
   *
   * @param postId The ID of the post
   * @param userId The ID of the user commenting
   * @param text The content of the comment
   * @return CommentResponse representing the newly created comment
   */
  @Transactional
  public CommentResponse addComment(long postId, long userId, String text) {
    PostCommentEntity entity = new PostCommentEntity();
    entity.setPostId(postId);
    entity.setUserId(userId);
    entity.setText(text);
    entity.setCreatedAt(Instant.now());
    PostCommentEntity saved = commentRepository.save(entity);

    CommentResponse res = new CommentResponse();
    res.setId(saved.getId());
    res.setPostId(postId);
    res.setUserId(userId);
    res.setText(text);
    res.setCreatedAt(saved.getCreatedAt());
    
    // Enrich with user profile so the frontend displays the correct name immediately
    res.setUser(profileClient.getPublicProfile(userId));

    // Trigger notification
    postRepository.findById(postId).ifPresent(post -> {
        if (!post.getAuthorId().equals(userId)) {
            PublicUserDto commenter = profileClient.getPublicProfile(userId);
            String name = (commenter != null && commenter.getUsername() != null) ? commenter.getUsername() : "Someone";
            sendNotification(post.getAuthorId(), "PUSH", "New Comment", name + " commented on your post!");
        }
    });

    return res;
  }

  /**
   * Saves a post to a user's collection.
   *
   * <p>Flow: Checks if the post is already saved by the user. If not, creates and persists a
   * SavedPostEntity.
   *
   * <p>Features: Allows users to bookmark posts for later viewing.
   *
   * @param postId The ID of the post to save
   * @param userId The ID of the user saving the post
   */
  @Transactional
  public void savePost(long postId, long userId) {
    if (savedPostRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
      return;
    }
    SavedPostEntity entity = new SavedPostEntity();
    entity.setPostId(postId);
    entity.setUserId(userId);
    entity.setCreatedAt(Instant.now());
    savedPostRepository.save(entity);
  }

  /**
   * Removes a post from a user's saved collection.
   *
   * <p>Flow: Locates the saved post entry for the user/post pair and deletes it if it exists.
   *
   * <p>Features: Allows users to manage their saved posts.
   *
   * @param postId The ID of the post to unsave
   * @param userId The ID of the user unsaving the post
   */
  @Transactional
  public void unsavePost(long postId, long userId) {
    savedPostRepository.findByPostIdAndUserId(postId, userId)
        .ifPresent(savedPostRepository::delete);
  }

  private void sendNotification(Long recipientId, String type, String title, String message) {
      try {
          Map<String, Object> body = new HashMap<>();
          body.put("recipientId", String.valueOf(recipientId));
          body.put("type", type);
          body.put("title", title);
          body.put("message", message);

          HttpHeaders headers = new HttpHeaders();
          headers.set("X-Internal-Secret", internalSecret);
          HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

          restTemplate.postForObject("http://localhost:8084/api/notifications", entity, String.class);
      } catch (Exception e) {
          log.error("Failed to send notification: {}", e.getMessage());
      }
  }
}
