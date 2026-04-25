package com.sportsDetect.crawler.controller;


import com.sportsDetect.crawler.model.CrawlResult;
import com.sportsDetect.crawler.model.DetectionLog;
import com.sportsDetect.crawler.model.Media;
import com.sportsDetect.crawler.repository.CrawlerResultRepository;
import com.sportsDetect.crawler.repository.DetectionLogRepository;
import com.sportsDetect.crawler.repository.MediaRepository;
import com.sportsDetect.crawler.service.AutomatedService;
import com.sportsDetect.crawler.service.RedisQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crawler")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CrawlerController {
    @Autowired
    private AutomatedService automatedService;

    @Autowired
    private RedisQueueService redisQueueService;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private DetectionLogRepository detectionLogRepository;

    @Autowired
    private CrawlerResultRepository crawlerResultRepository;

    /*@PostMapping("/start")
    public String runCrawler() throws InterruptedException {
        automatedService.executeHunt();
        return "Crawler Started";
    }*/
    @PostMapping("/start")
    public ResponseEntity<?> runCrawler(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");
        if (query != null) {
            automatedService.startDynamicCrawl(query);
            return ResponseEntity.ok("Search hunt started for: " + query);
        } else {
            automatedService.executeHunt();
            return ResponseEntity.ok("Global hunt started");
        }
    }

    @GetMapping("/queue-size")
    public Long getQueueSize(){
         return redisQueueService.size();
    }

    @GetMapping("/results")
    public List<CrawlResult> getResults() {
        return crawlerResultRepository.findAll();
    }

    @GetMapping("/detections")
    public List<DetectionLog> getDetections() {
        return detectionLogRepository.findAll();
    }

}
