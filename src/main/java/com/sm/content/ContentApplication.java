package com.sm.content;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Content Service microservice.
 *
 * <p>Flow: Initializes the Spring Boot application, loading local environment variables before
 * starting the context.
 *
 * <p>Features: Main service runner for handling post content, media, and engagement.
 */
@SpringBootApplication
public class ContentApplication {

  /**
   * Main method to launch the application.
   *
   * <p>Flow: Calls loadLocalEnvFile to prepare the environment, then executes SpringApplication.run.
   *
   * <p>Features: Bootstraps the microservice.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    loadLocalEnvFile();
    SpringApplication.run(ContentApplication.class, args);
  }

  /**
   * Loads {@code .env} from the working directory.
   *
   * <p>Flow: Uses Dotenv to read the .env file. It iterates through the entries and sets them as
   * system properties if they aren't already defined in the environment or system properties.
   *
   * <p>Features: Local configuration management for AWS credentials and other secrets.
   */
  private static void loadLocalEnvFile() {
    Dotenv dotenv =
        Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
    dotenv
        .entries()
        .forEach(
            e -> {
              String key = e.getKey();
              if (key == null || key.isBlank() || e.getValue() == null) {
                return;
              }
              if (System.getenv(key) != null) {
                return;
              }
              if (System.getProperty(key) != null) {
                return;
              }
              System.setProperty(key, e.getValue());
            });
  }
}
