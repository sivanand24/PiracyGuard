package com.sportsDetect.crawler;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;



@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableAsync
@EnableWebSecurity
public class CrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

	}
