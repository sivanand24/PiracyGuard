package com.sportsDetect.crawler.controller;

import com.sportsDetect.crawler.service.VideoService;
import com.sportsDetect.crawler.utils.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/validate")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class MediaController {

    @Autowired
    private HashUtil hashUtil;

    @Autowired
    private VideoService videoService;

    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareImages(
            @RequestParam("official") MultipartFile officialFile,
            @RequestParam("suspect") MultipartFile suspectFile) {

        try {
            String officialHash = HashUtil.generatePHash(officialFile.getBytes());
            String suspectHash = HashUtil.generatePHash(suspectFile.getBytes());

            int distance = HashUtil.getHammingDistance(officialHash, suspectHash);

            double confidence = 100 - ((double) distance / 64 * 100);

            Map<String, Object> response = new HashMap<>();
            response.put("confidenceScore", String.format("%.2f%%", confidence));
            response.put("distance", distance);
            response.put("riskLevel", hashUtil.getRiskLevel(distance));
            response.put("actionRecommended", distance <= 25 ? "AUTO-TAKEDOWN" :
                    distance <= 40 ? "MANUAL_REVIEW" : "IGNORED");
            response.put("officialHash", officialHash);
            response.put("suspectHash", suspectHash);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/compare-video")
    public ResponseEntity<Map<String, Object>> compareVideos(
            @RequestParam("official") MultipartFile officialVideo,
            @RequestParam("suspect") MultipartFile suspectVideo) {


        if (officialVideo == null || officialVideo.isEmpty() || suspectVideo == null || suspectVideo.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Both 'official' and 'suspect' files are required.");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            VideoService.VideoComparisonResult result = videoService.compareVideos(officialVideo, suspectVideo);

            Map<String, Object> response = new HashMap<>();
            response.put("confidenceScore", String.format("%.2f%%", result.matchPercentage));
            response.put("distance", result.averageDistance); // Now this works with Integer!
            response.put("riskLevel", result.matchPercentage > 80 ? "CRITICAL" : "LOW_RISK");
            response.put("actionRecommended", result.matchPercentage > 80 ? "AUTO-TAKEDOWN" : "MANUAL_REVIEW");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    }

