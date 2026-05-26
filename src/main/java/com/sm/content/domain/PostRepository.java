package com.sm.content.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing PostEntity persistence.
 *
 * <p>Flow: Provides methods to retrieve posts by author and find the most recently created posts.
 *
 * <p>Features: Core data access for post content.
 */
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
  
  /**
   * Retrieves all posts by a specific author, ordered by creation date descending.
   *
   * @param authorId The ID of the author
   * @return List of post entities
   */
  List<PostEntity> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
  List<PostEntity> findAllByOrderByCreatedAtDesc();

  /**
   * Finds the post with the highest ID.
   *
   * <p>Flow: Used for manual ID generation to determine the next available ID.
   *
   * @return Optional containing the last post entity
   */
  Optional<PostEntity> findFirstByOrderByIdDesc();
}
