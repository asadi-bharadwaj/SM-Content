package com.sm.content.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Client for communicating with the Profile Service to retrieve user information.
 *
 * <p>Flow: Uses RestTemplate to make HTTP GET requests to the Profile Service API. It includes a
 * fallback mechanism that returns a stub profile if the service is unavailable.
 *
 * <p>Features: Inter-service communication and graceful degradation for user profile enrichment.
 */
@Component
public class ProfileServiceClient {

  private static final Logger log = LoggerFactory.getLogger(ProfileServiceClient.class);

  private final RestTemplate restTemplate;
  private final String profileBaseUrl;

  @Value("${internal.api.secret:super-secret-internal-key}")
  private String internalSecret;

  /**
   * Constructs the ProfileServiceClient.
   *
   * @param restTemplate The RestTemplate to use for requests
   * @param profileBaseUrl The base URL of the Profile Service
   */
  public ProfileServiceClient(
      RestTemplate restTemplate,
      @Value("${profile.service.base-url:http://localhost:8082}") String profileBaseUrl) {
    this.restTemplate = restTemplate;
    this.profileBaseUrl = profileBaseUrl.replaceAll("/$", "");
  }

  /**
   * Fetches the public profile of a user.
   *
   * <p>Flow: Sends a GET request to the /users/public/{userId} endpoint of the Profile Service.
   * If the request fails (e.g., timeout, 5xx), it logs a warning and returns a default stub profile.
   *
   * <p>Features: Reliable user profile retrieval for content enrichment.
   *
   * @param userId The ID of the user to fetch
   * @return The PublicUserDto containing profile data (or a stub if service is down)
   */
  public PublicUserDto getPublicProfile(long userId) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("X-Internal-Secret", internalSecret);
      HttpEntity<Void> entity = new HttpEntity<>(headers);
      
      ResponseEntity<PublicUserDto> response = restTemplate.exchange(
          profileBaseUrl + "/users/public/" + userId, HttpMethod.GET, entity, PublicUserDto.class);
      return response.getBody();
    } catch (RestClientException e) {
      log.warn("Profile {} not available: {}", userId, e.getMessage());
      PublicUserDto stub = new PublicUserDto();
      stub.setId(String.valueOf(userId));
      stub.setAuthUserId(userId);
      stub.setUsername("user" + userId);
      stub.setDisplayName("User");
      return stub;
    }
  }

  /**
   * Fetches multiple public profiles in bulk.
   */
  public List<PublicUserDto> getBulkProfiles(List<Long> userIds) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("X-Internal-Secret", internalSecret);
      
      ResponseEntity<List<PublicUserDto>> response = restTemplate.exchange(
          profileBaseUrl + "/users/bulk",
          HttpMethod.POST,
          new HttpEntity<>(userIds, headers),
          new ParameterizedTypeReference<List<PublicUserDto>>() {}
      );
      return response.getBody();
    } catch (RestClientException e) {
      log.warn("Bulk profile fetch failed: {}", e.getMessage());
      return List.of();
    }
  }
}
