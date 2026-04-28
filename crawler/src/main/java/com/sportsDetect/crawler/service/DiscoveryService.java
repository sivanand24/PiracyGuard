package com.sportsDetect.crawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscoveryService {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> findViolations(String query, String site) {
        try {
            String fullQuery = query;
            String url = "https://searx.be/search?q=" +
                    URLEncoder.encode(fullQuery, StandardCharsets.UTF_8) +
                    "&format=json";

            System.out.println("DEBUG: Calling SearXNG URL: " + url);

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            System.out.println("DEBUG: Raw JSON Response: " + response.toString());

            List<String> links = new ArrayList<>();
            if (response != null && response.has("results")) {
                for (JsonNode item : response.get("results")) {
                    links.add(item.get("url").asText());
                }
            }
            return links;
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in DiscoveryService: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
