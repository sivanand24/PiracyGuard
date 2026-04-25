package com.sportsDetect.crawler.controller;

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

    }

