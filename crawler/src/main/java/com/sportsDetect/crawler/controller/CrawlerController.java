package com.sportsDetect.crawler.controller;



import com.sportsDetect.crawler.model.Media;
import com.sportsDetect.crawler.repository.MediaRepository;

import com.sportsDetect.crawler.service.DiscoveryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class CrawlerController {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private DiscoveryService discoveryService;

    @GetMapping("/db")
    public String testDB() {
        long count = mediaRepository.count();
        return "DB Connected ✅ | Records: " + count;
    }


    @GetMapping("/search")
    public ResponseEntity<List<String>> search(@RequestParam String query) {
        try {
            List<String> results = discoveryService.findViolations(query, "illegal-streaming-site.com");
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    /*@PostMapping("/start")
    public ResponseEntity<?> runCrawler(@RequestBody(required = false) CrawlerRequest request) {
        String query = (request != null) ? request.getQuery() : null;
        if (query != null) {
            automatedService.startDynamicCrawl(query);
            return ResponseEntity.ok("Search hunt started for: " + query);
        } else {
            automatedService.executeHunt();
            return ResponseEntity.ok("Global hunt started");
        }
    }

    @GetMapping("/queue-size")
    public ResponseEntity<Long> getQueueSize(){
        long size = redisQueueService.size();
        System.out.println("DEBUG: Backend is reporting queue size as: " + size);
        return ResponseEntity.ok(redisQueueService.size());
    }

    @GetMapping("/results")
    public List<CrawlResult> getResults() {
        return crawlerResultRepository.findAll();
    }

    @GetMapping("/detections")
    public List<DetectionLog> getDetections() {
        return detectionLogRepository.findAll();
    }
*/
}
