package com.sm.content;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContentApplication {

  public static void main(String[] args) {
    loadLocalEnvFile();
    SpringApplication.run(ContentApplication.class, args);
  }

  /**
   * Loads {@code .env} from the working directory (e.g. {@code content-service/}) so AWS and
   * other vars are available to Spring without exporting them in the shell. Real environment
   * variables always win over {@code .env}.
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
