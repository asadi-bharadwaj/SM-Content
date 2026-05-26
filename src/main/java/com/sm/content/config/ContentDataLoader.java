package com.sm.content.config;

import com.sm.content.domain.PostEntity;
import com.sm.content.domain.PostRepository;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * **Summary:** Data loader for initializing sample content.
 * 
 * **Flow:** This configuration class provides a `CommandLineRunner` that checks if the 
 * post repository is empty and, if so, seeds it with initial sample data for demonstration purposes.
 * 
 * **Features:** Automated database seeding, development environment setup.
 */
@Configuration
public class ContentDataLoader {

  /**
   * **Summary:** Seeds the database with sample posts if empty.
   * 
   * **Flow:** 
   * 1. Checks if any posts already exist in the repository.
   * 2. If the repository is empty, it calls `samplePost` multiple times to create initial entries.
   * 
   * **Features:** Initial data population.
   * 
   * @param posts The PostRepository used to save sample data.
   * @return A CommandLineRunner that executes the seeding logic.
   */
  @Bean
  CommandLineRunner seedPosts(PostRepository posts) {
    return args -> {
      if (posts.count() > 0) {
        return;
      }
      // Let the database handle ID generation
      // samplePost(posts, 1L, "https://picsum.photos/seed/sm-a1/800/800", "Hello world");
      // samplePost(posts, 1L, "https://picsum.photos/seed/sm-a2/800/800", "Second post");
      // samplePost(posts, 2L, "https://picsum.photos/seed/sm-b1/800/800", "Bob's shot");
    };
  }

  /**
   * **Summary:** Helper method to create and save a sample post.
   * 
   * **Flow:** 
   * 1. Instantiates a new `PostEntity`.
   * 2. Sets its properties such as author ID, media URL, type, caption, and visibility.
   * 3. Saves the entity using the provided repository.
   * 
   * **Features:** Post entity creation utility.
   * 
   * @param posts The repository to save to.
   * @param authorId ID of the post author.
   * @param url URL of the media content.
   * @param caption Caption text for the post.
   */
  private static void samplePost(
      PostRepository posts, Long authorId, String url, String caption) {
    PostEntity p = new PostEntity();
    // Do NOT set ID manually when using GenerationType.IDENTITY
    p.setAuthorId(authorId);
    p.setMediaUrl(url);
    p.setMediaType("image");
    p.setCaption(caption);
    p.setCreatedAt(Instant.now());
    p.setVisibility("public");
    p.setTags("");
    posts.save(p);
  }
}
