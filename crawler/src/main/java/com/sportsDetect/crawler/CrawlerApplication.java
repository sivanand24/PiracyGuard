package com.sportsDetect.crawler;

import com.sportsDetect.crawler.config.SecurityConfig;
import com.sportsDetect.crawler.model.Media;
import com.sportsDetect.crawler.repository.MediaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@Import(SecurityConfig.class)
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = "com.sportsDetect.crawler")
public class CrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadTestData(MediaRepository mediaRepository) {
		return args -> {
			if (mediaRepository.count() == 0) {
				System.out.println(" [SYSTEM] Auto-loaded 'Lakers vs Celtics' into the database!");
			}
		};
	}
}
