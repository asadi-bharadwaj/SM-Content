package com.sm.content.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * **Summary:** Web configuration for the Content service.
 * 
 * **Flow:** Implements `WebMvcConfigurer` to customize Spring MVC behavior. It configures 
 * resource handlers for serving uploaded media files, sets up CORS mappings for 
 * frontend integration, and provides a `RestTemplate` bean for inter-service communication.
 * 
 * **Features:** Static resource serving, CORS policy management, REST client provisioning.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${content.upload.directory:./data/uploads}")
  private String uploadDirectory;

  /**
   * **Summary:** Configures resource handlers for media files.
   * 
   * **Flow:** 
   * 1. Resolves the absolute path of the configured upload directory.
   * 2. Converts the path to a URI string.
   * 3. Maps the "/media/**" URL pattern to the physical location of the uploaded files.
   * 
   * **Features:** Dynamic file serving from the file system.
   * 
   * @param registry The ResourceHandlerRegistry to configure.
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    Path dir = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    String location = dir.toUri().toString();
    if (!location.endsWith("/")) {
      location += "/";
    }
    registry.addResourceHandler("/media/**").addResourceLocations(location);
  }

  /**
   * **Summary:** Configures Cross-Origin Resource Sharing (CORS) rules.
   * 
   * **Flow:** 
   * 1. Allows requests from "http://localhost:5173" and "http://127.0.0.1:5173" (typical Vite dev servers).
   * 2. Permits common HTTP methods: GET, POST, PUT, PATCH, DELETE, OPTIONS.
   * 3. Allows all headers.
   * 
   * **Features:** Frontend-backend connectivity support.
   * 
   * @param registry The CorsRegistry to configure.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowCredentials(true)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*");
  }

  /**
   * **Summary:** Provides a RestTemplate bean.
   * 
   * **Flow:** Instantiates and returns a default `RestTemplate` for making synchronous HTTP calls to other services.
   * 
   * **Features:** Synchronous REST client.
   * 
   * @return A new RestTemplate instance.
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
