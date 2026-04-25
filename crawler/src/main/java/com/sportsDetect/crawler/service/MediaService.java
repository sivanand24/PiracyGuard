package com.sportsDetect.crawler.service;

import com.sportsDetect.crawler.engine.AISimilarityEngine;
import com.sportsDetect.crawler.engine.Validator;
import com.sportsDetect.crawler.engine.WebScout;
import com.sportsDetect.crawler.model.DetectionLog;
import com.sportsDetect.crawler.model.Media;
import com.sportsDetect.crawler.repository.DetectionLogRepository;
import com.sportsDetect.crawler.utils.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class MediaService {
    @Autowired
    private WebScout webScout;

    @Autowired
    private Validator validator;

    @Autowired
    private HashUtil hashUtil;

    @Autowired
     private DetectionLogRepository logRepository;


    private boolean isAMatch(String officialHash, String foundHash) {
        int distance = hashUtil.getHammingDistance(officialHash,foundHash);
        int threshold = 10;
        return distance <= threshold;
    }


    public void runTier1Discovery(String mediaTitle, String officialHash) {
        Set<String> links = webScout.scanSiteForLinks("https://vipleague.im",
                mediaTitle);
        for (String url : links) {
            if (isLinkAlreadyProcessed(url)) {
                continue;
            }
            processedLinks.add(url);

            try {
                //String officialPath = "C:/Users/Sivanand/Pictures/Camera Roll/official one.jpeg";
                String suspectPath = "C:/Users/Sivanand/Pictures/Camera Roll/suspected one.jpeg";
                String foundHash = hashUtil.generatePHash(suspectPath);


                if(isAMatch(officialHash, foundHash)){
                    System.out.println("ALERT: Tier 1 Piracy confirmed at " + url);

                } else {
                    System.out.println("Tier 1 clear. Not a match.");
                }

            } catch (Exception e) {
                System.err.println("Failed to process hash for: " + url);
            }
        }
    }
    public void processDiscovery(String url, Media officialMedia, byte[] suspectImageBytes){
        processedLinks.add(url);

        try {
            String suspectHash = HashUtil.generatePHash(suspectImageBytes);
            //String officialPath = "C:/Users/Sivanand/Pictures/Camera Roll/official one.jpeg";
            //String suspectPath = "C:/Users/Sivanand/Pictures/Camera Roll/suspected one.jpeg";

            if(suspectHash == null){
                System.out.println("[TIER 1] Error: URL did not contain valid image data. Skipping.");
                return;
            }
            int distance = HashUtil.getHammingDistance(officialMedia.getOfficialHash(), suspectHash);

            if (distance <= 40 && distance != -1) {
                System.out.println("[TIER 1] High visual similarity detected (Distance: " + distance + ")");
                System.out.println("[TIER 2] Waking up Gemini AI for contextual analysis...");
                String officialPath = "C:/Users/Sivanand/Pictures/Camera Roll/official one.jpeg";
                String aiResponse = AISimilarityEngine.compareImages(officialPath, suspectImageBytes);

                if (aiResponse != null && aiResponse.toLowerCase().contains("\"isbaseimagematch\": true") || aiResponse.toLowerCase().contains("\"isbaseimagematch\":true")) {
                    System.out.println("=== SYSTEM VERDICT ===");
                    System.out.println(aiResponse);

                    DetectionLog log = new DetectionLog();
                    log.setSourceUrl(url);
                    log.setDetectedAt(LocalDateTime.now());
                    log.setHammingDistance(distance);
                    log.setRiskLevel(hashUtil.getRiskLevel(distance));
                    log.setOriginalMedia(officialMedia);

                    logRepository.save(log);
                    System.out.println(">>> LOGGED TO DATABASE: Confirmed piracy at " + url);
                } else {
                    System.out.println("[TIER 2] AI cleared this as fair use/safe. Discarding.");
                }
            } else {
                System.out.println("[TIER 1] Not a visual match. Ignoring.");
            }

        } catch (Exception e) {
            System.err.println("Pipeline error: " + e.getMessage());
        }
    }
    private final Set<String> processedLinks = new HashSet<>();

    public boolean isLinkAlreadyProcessed(String link) {
        return processedLinks.contains(link);
    }

}



