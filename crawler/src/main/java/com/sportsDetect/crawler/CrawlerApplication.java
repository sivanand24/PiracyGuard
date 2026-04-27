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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableAsync
@EnableWebSecurity
public class CrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

		@Bean
		public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
			http
					.csrf(csrf -> csrf.disable())
					.cors(Customizer.withDefaults())
					.authorizeHttpRequests(auth -> auth
							.anyRequest().permitAll()
					);
			return http.build();
		}
	}
