package com.sportsDetect.crawler.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscoveryService {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> findViolations(String query, String site) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://searxng-production-fd35.up.railway.app/search?q=" + encodedQuery + "&format=json";

            HttpHeaders headers;
            headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json");
            headers.set("Accept-Language", "en-US,en;q=0.9");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

            List<String> links = new ArrayList<>();
            JsonNode responseBody = response.getBody();

            if (responseBody != null && responseBody.has("results")) {
                for (JsonNode item : responseBody.get("results")) {
                    if (item.has("url")) {
                        links.add(item.get("url").asText());
                    }
                }
            }
            return links;
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}