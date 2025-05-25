package kg.spring.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TgbotahueniyApplication {

  public static void main(String[] args) {
    SpringApplication.run(TgbotahueniyApplication.class, args);
  }

}
