package com.sm.content.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * **Summary:** Request body for creating or updating a comment.
 * 
 * **Flow:** This DTO is used to capture the text content of a comment from the request body. 
 * It includes validation annotations to ensure the comment is not blank and stays within 
 * the allowed character limit.
 * 
 * **Features:** Input validation, comment content submission.
 */
public class CommentBody {

  @NotBlank(message = "text required")
  @Size(max = 2200)
  private String text;

  /**
   * **Summary:** Gets the comment text.
   * 
   * **Flow:** Returns the raw text of the comment.
   * 
   * **Features:** Content retrieval.
   * 
   * @return The comment text.
   */
  public String getText() {
    return text;
  }

  /**
   * **Summary:** Sets the comment text.
   * 
   * **Flow:** Assigns the raw text of the comment.
   * 
   * **Features:** Content assignment.
   * 
   * @param text The text to set.
   */
  public void setText(String text) {
    this.text = text;
  }
}
