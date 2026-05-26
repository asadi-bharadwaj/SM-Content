package com.sm.content.web;

import com.sm.content.domain.PostCommentEntity;
import com.sm.content.domain.PostCommentRepository;
import com.sm.content.domain.PostEntity;
import com.sm.content.domain.PostLikeEntity;
import com.sm.content.domain.PostLikeRepository;
import com.sm.content.domain.PostRepository;
import com.sm.content.domain.SavedPostEntity;
import com.sm.content.domain.SavedPostRepository;
import com.sm.content.web.dto.CommentBody;
import com.sm.content.web.dto.CommentResponse;
import com.sm.content.web.dto.EngagementResponse;
import com.sm.content.web.dto.LikersResponse;
import jakarta.validation.Valid;
import java.util.List;
import com.sm.content.web.dto.CreatePostRequest;
import com.sm.content.web.dto.PreSignedUrlRequest;
import com.sm.content.service.S3Service;
import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * **Summary:** REST Controller for managing post interactions and engagements.
 * 
 * **Flow:** This controller handles user interactions such as liking, unliking, commenting, 
 * and saving posts. it also provides endpoints for retrieving engagement metrics and 
 * purging user data (for administrative or account deletion purposes). It delegates 
 * business logic to the `PostEngagementService`.
 * 
 * **Features:** Post engagement (likes, comments, saves), data purging, metric retrieval.
 */
@RestController
@RequestMapping("/posts")
@Slf4j
public class PostsRestController {

  private final PostEngagementService engagementService;
  private final PostRepository postRepository;
  private final PostLikeRepository likeRepository;
  private final PostCommentRepository commentRepository;
  private final SavedPostRepository savedPostRepository;
  private final S3Service s3Service;
  private final com.sm.content.client.ProfileServiceClient profileClient;
  private final com.sm.content.service.PostWriteService postWriteService;
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${internal.api.secret:super-secret-internal-key}")
  private String internalSecret;

  /**
   * **Summary:** Constructs the PostsRestController with necessary dependencies.
   * 
   * **Flow:** Initializes the controller with repositories and engagement services via constructor injection.
   * 
   * **Features:** Dependency injection.
   * 
   * @param engagementService The service handling post engagements.
   * @param postRepository Repository for post data.
   * @param likeRepository Repository for post likes.
   * @param commentRepository Repository for post comments.
   * @param savedPostRepository Repository for saved posts.
   */
  public PostsRestController(
      PostEngagementService engagementService,
      PostRepository postRepository,
      PostLikeRepository likeRepository,
      PostCommentRepository commentRepository,
      SavedPostRepository savedPostRepository,
      S3Service s3Service,
      com.sm.content.client.ProfileServiceClient profileClient,
      com.sm.content.service.PostWriteService postWriteService) {
    this.engagementService = engagementService;
    this.postRepository = postRepository;
    this.likeRepository = likeRepository;
    this.commentRepository = commentRepository;
    this.savedPostRepository = savedPostRepository;
    this.s3Service = s3Service;
    this.profileClient = profileClient;
    this.postWriteService = postWriteService;
  }

  /**
   * **Summary:** Extracts the authenticated user ID from the Principal.
   * 
   * **Flow:** Takes the Spring Security `Principal` object and returns the user ID stored in its name.
   * 
   * **Features:** Security context integration.
   * 
   * @param principal The security principal.
   * @return The user ID as a Long, or null if principal is null.
   */
  private Long getAuthenticatedUserId(Principal principal) {
    return principal == null ? null : Long.valueOf(principal.getName());
  }

  /**
   * **Summary:** Retrieves engagement metrics for a specific post.
   * 
   * **Flow:** 
   * 1. Extracts the current user ID.
   * 2. Calls `engagementService.getEngagement` to fetch like/comment counts and user-specific status.
   * 
   * **Features:** Post metrics visibility.
   * 
   * @param postId The ID of the post.
   * @param principal The security principal.
   * @return An EngagementResponse containing metrics.
   */
  @GetMapping("/engagement/{postId}")
  public EngagementResponse getEngagement(
      @PathVariable long postId,
      Principal principal) {
    return engagementService.getEngagement(postId, getAuthenticatedUserId(principal));
  }

  @GetMapping("/engagement/{postId}/likers")
  public LikersResponse getLikers(
      @PathVariable long postId,
      @RequestParam(defaultValue = "20") int limit) {
    return engagementService.listLikers(postId, limit);
  }

  /**
   * **Summary:** Generates a pre-signed URL for uploading a media file to S3.
   * @param request Contains extension and content type
   * @return The pre-signed URL and object key
   */
  @GetMapping("/feed")
  public List<com.sm.content.web.dto.PostResponse> getFeed() {
      List<PostEntity> posts = postRepository.findAllByOrderByCreatedAtDesc();
      return mapToPostResponses(posts);
  }

  @GetMapping("/{postId}")
  public com.sm.content.web.dto.PostResponse getPost(@PathVariable Long postId) {
      PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
      return mapToPostResponses(List.of(post)).get(0);
  }

  @GetMapping("/{postId}/comments")
  public List<CommentResponse> getComments(@PathVariable Long postId) {
      List<PostCommentEntity> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
      if (comments.isEmpty()) return List.of();
      
      List<Long> userIds = comments.stream().map(PostCommentEntity::getUserId).distinct().collect(Collectors.toList());
      List<com.sm.content.client.PublicUserDto> profiles = profileClient.getBulkProfiles(userIds);
      
      Map<Long, com.sm.content.client.PublicUserDto> profileMap = new HashMap<>();
      if (profiles != null) {
          for (com.sm.content.client.PublicUserDto p : profiles) {
              Long key = p.getAuthUserId() != null ? p.getAuthUserId() : Long.valueOf(p.getId());
              profileMap.put(key, p);
          }
      }
      
      return comments.stream().map(c -> {
          CommentResponse res = new CommentResponse();
          res.setId(c.getId());
          res.setPostId(c.getPostId());
          res.setUserId(c.getUserId());
          res.setText(c.getText());
          res.setCreatedAt(c.getCreatedAt());
          res.setUser(profileMap.get(c.getUserId()));
          return res;
      }).collect(Collectors.toList());
  }

  @GetMapping("/user/{userId}")
  public List<com.sm.content.web.dto.PostResponse> getUserPosts(@PathVariable Long userId) {
      List<PostEntity> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
      return mapToPostResponses(posts);
  }

  @GetMapping("/saved")
  public List<com.sm.content.web.dto.PostResponse> getSavedPosts(Principal principal) {
      Long userId = getAuthenticatedUserId(principal);
      if (userId == null) throw new RuntimeException("Unauthorized");
      
      List<SavedPostEntity> saved = savedPostRepository.findByUserIdOrderByCreatedAtDesc(userId);
      if (saved.isEmpty()) return List.of();
      
      List<Long> postIds = saved.stream().map(SavedPostEntity::getPostId).collect(Collectors.toList());
      List<PostEntity> posts = postRepository.findAllById(postIds);
      
      Map<Long, PostEntity> postMap = posts.stream().collect(Collectors.toMap(PostEntity::getId, p -> p));
      List<PostEntity> orderedPosts = saved.stream()
          .map(s -> postMap.get(s.getPostId()))
          .filter(p -> p != null)
          .collect(Collectors.toList());
          
      return mapToPostResponses(orderedPosts);
  }

  private List<com.sm.content.web.dto.PostResponse> mapToPostResponses(List<PostEntity> posts) {
      if (posts == null || posts.isEmpty()) return List.of();
      
      List<Long> authorIds = posts.stream().map(PostEntity::getAuthorId).distinct().collect(Collectors.toList());
      List<com.sm.content.client.PublicUserDto> profiles = profileClient.getBulkProfiles(authorIds);
      
      Map<Long, com.sm.content.client.PublicUserDto> profileMap = new HashMap<>();
      if (profiles != null) {
          for (com.sm.content.client.PublicUserDto p : profiles) {
              Long key = p.getAuthUserId() != null ? p.getAuthUserId() : Long.valueOf(p.getId());
              profileMap.put(key, p);
          }
      }
      
      return posts.stream().map(p -> {
          com.sm.content.web.dto.PostResponse res = new com.sm.content.web.dto.PostResponse();
          res.setId(p.getId());
          res.setAuthorId(p.getAuthorId());
          res.setMediaUrl(p.getMediaUrl());
          res.setMediaType(p.getMediaType());
          res.setCaption(p.getCaption());
          res.setVisibility(p.getVisibility());
          res.setTags(p.getTags());
          res.setLocation(p.getLocation());
          res.setMusicTrack(p.getMusicTrack());
          res.setCreatedAt(p.getCreatedAt());
          
          com.sm.content.web.dto.PostResponse.AuthorDto author = new com.sm.content.web.dto.PostResponse.AuthorDto();
          com.sm.content.client.PublicUserDto pub = profileMap.get(p.getAuthorId());
          if (pub != null) {
              author.setUsername(pub.getUsername());
              author.setDisplayName(pub.getDisplayName() != null ? pub.getDisplayName() : pub.getUsername());
              author.setAvatarUrl(pub.getAvatarUrl());
          } else {
              author.setUsername("user" + p.getAuthorId());
              author.setDisplayName("User");
          }
          res.setAuthor(author);
          
          return res;
      }).collect(Collectors.toList());
  }

  @PostMapping("/upload-url")
  public S3Service.PreSignedUrlResponse getUploadUrl(@RequestBody PreSignedUrlRequest request, Principal principal) {
      if (principal == null) {
          throw new RuntimeException("Unauthorized");
      }
      return s3Service.generatePreSignedUrl(request.getExtension(), request.getContentType());
  }

  /**
   * **Summary:** Creates a new post with the given media URL and metadata.
   * @param request The post creation payload
   * @param principal The security principal
   * @return The created PostResponse
   */
  @PostMapping
  public com.sm.content.web.dto.PostResponse createPost(@Valid @RequestBody CreatePostRequest request, Principal principal) {
      if (principal == null) {
          throw new RuntimeException("Unauthorized");
      }
      
      PostEntity post = new PostEntity();
      post.setAuthorId(getAuthenticatedUserId(principal));
      post.setMediaUrl(request.getMediaUrl());
      post.setMediaType(request.getMediaType());
      post.setCaption(request.getCaption());
      post.setVisibility(request.getVisibility());
      post.setTags(request.getTags());
      post.setLocation(request.getLocation());
      post.setMusicTrack(request.getMusicTrack());
      post.setCreatedAt(Instant.now());
      
      PostEntity saved = postRepository.save(post);
      log.info("CONTENT-SERVICE: Created new post {} by user {}", saved.getId(), saved.getAuthorId());
      
      notifyFollowers(saved.getAuthorId(), saved.getId());
      
      return mapToPostResponses(List.of(saved)).get(0);
  }

  private void notifyFollowers(Long authorId, Long postId) {
      try {
          // Fetch followers from User-Service
          String userSvcUrl = "http://localhost:8083/users/" + authorId + "/followers";
          ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
              userSvcUrl,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<List<Map<String, Object>>>() {}
          );

          List<Map<String, Object>> followers = response.getBody();
          if (followers != null) {
              for (Map<String, Object> follower : followers) {
                  Object followerIdObj = follower.get("userId");
                  if (followerIdObj != null) {
                      Long followerId = Long.valueOf(followerIdObj.toString());
                      sendNotification(followerId, "PUSH", "New Post", "A creator you follow just posted!");
                  }
              }
          }
      } catch (Exception e) {
          log.error("Failed to notify followers for post {}: {}", postId, e.getMessage());
      }
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

  /**
   * **Summary:** Adds a like to a post.
   * 
   * **Flow:** Extracts the current user ID and calls the engagement service to record a like.
   * 
   * **Features:** Social interaction (liking).
   * 
   * @param postId The ID of the post to like.
   * @param principal The security principal.
   */
  @PostMapping("/like/{postId}")
  public void like(@PathVariable long postId, Principal principal) {
    engagementService.like(postId, getAuthenticatedUserId(principal));
  }

  /**
   * **Summary:** Removes a like from a post.
   * 
   * **Flow:** Extracts the current user ID and calls the engagement service to delete the like.
   * 
   * **Features:** Social interaction (unliking).
   * 
   * @param postId The ID of the post to unlike.
   * @param principal The security principal.
   */
  @DeleteMapping("/like/{postId}")
  public void unlike(@PathVariable long postId, Principal principal) {
    engagementService.unlike(postId, getAuthenticatedUserId(principal));
  }

  /**
   * **Summary:** Adds a comment to a post.
   * 
   * **Flow:** Extracts the user ID and text from the request, then calls the engagement service to save the comment.
   * 
   * **Features:** Social interaction (commenting).
   * 
   * @param postId The ID of the post to comment on.
   * @param principal The security principal.
   * @param body The comment content.
   * @return A CommentResponse containing the saved comment details.
   */
  @PostMapping("/comment/{postId}")
  public CommentResponse comment(
      @PathVariable long postId,
      Principal principal,
      @Valid @RequestBody CommentBody body) {
    return engagementService.addComment(postId, getAuthenticatedUserId(principal), body.getText());
  }

  /**
   * **Summary:** Saves a post for the current user.
   * 
   * **Flow:** Extracts the user ID and calls the engagement service to bookmark the post.
   * 
   * **Features:** Post bookmarking.
   * 
   * @param postId The ID of the post to save.
   * @param principal The security principal.
   */
  @PostMapping("/save/{postId}")
  public void save(@PathVariable long postId, Principal principal) {
    engagementService.savePost(postId, getAuthenticatedUserId(principal));
  }

  /**
   * **Summary:** Removes a saved post for the current user.
   * 
   * **Flow:** Extracts the user ID and calls the engagement service to remove the bookmark.
   * 
   * **Features:** Post un-bookmarking.
   * 
   * @param postId The ID of the post to unsave.
   * @param principal The security principal.
   */
  @DeleteMapping("/save/{postId}")
  public void unsave(@PathVariable long postId, Principal principal) {
    engagementService.unsavePost(postId, getAuthenticatedUserId(principal));
  }

  /**
   * **Summary:** Updates an existing post.
   * 
   * @param postId The ID of the post
   * @param request Payload containing new caption and location
   * @param principal The security principal
   * @return The updated post
   */
  @PutMapping("/{postId}")
  public com.sm.content.web.dto.PostResponse updatePost(
      @PathVariable long postId,
      @RequestBody Map<String, String> request,
      Principal principal) {
    Long authorId = getAuthenticatedUserId(principal);
    if (authorId == null) throw new RuntimeException("Unauthorized");
    
    postWriteService.updatePost(postId, authorId, request.get("caption"), request.get("location"));
    return getPost(postId);
  }

  /**
   * **Summary:** Deletes a post.
   * 
   * @param postId The ID of the post
   * @param principal The security principal
   */
  @DeleteMapping("/{postId}")
  public void deletePost(@PathVariable long postId, Principal principal) {
    Long authorId = getAuthenticatedUserId(principal);
    if (authorId == null) throw new RuntimeException("Unauthorized");
    
    postWriteService.deletePost(postId, authorId);
  }

  /**
   * **Summary:** Purges all data associated with a user.
   * 
   * **Flow:** 
   * 1. Deletes all posts authored by the user.
   * 2. Deletes all likes made by the user across all posts.
   * 3. Deletes all comments made by the user.
   * 4. Deletes all saved post entries for the user.
   * 
   * **Features:** Data privacy, account deletion support.
   * 
   * @param userId The ID of the user whose data is to be purged.
   */
  @DeleteMapping("/purge/{userId}")
  public void purge(@PathVariable Long userId) {
      // 1. Delete user's posts
      List<PostEntity> userPosts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
      postRepository.deleteAll(userPosts);
      
      // 2. Delete user's likes
      List<PostLikeEntity> likes = likeRepository.findAll().stream().filter(l -> l.getUserId().equals(userId)).toList();
      likeRepository.deleteAll(likes);
      
      // 3. Delete user's comments
      List<PostCommentEntity> comments = commentRepository.findAll().stream().filter(c -> c.getUserId().equals(userId)).toList();
      commentRepository.deleteAll(comments);
      
      // 4. Delete user's saved posts
      List<SavedPostEntity> saved = savedPostRepository.findByUserIdOrderByCreatedAtDesc(userId);
      savedPostRepository.deleteAll(saved);
      
      log.info("CONTENT-SERVICE: Data purged for user {}", userId);
  }
}
