package com.sportsDetect.crawler.service;

/*import com.sportsDetect.crawler.engine.Validator;
import com.sportsDetect.crawler.engine.WebScout;
import com.sportsDetect.crawler.model.CrawlResult;
import com.sportsDetect.crawler.model.Media;
import com.sportsDetect.crawler.repository.CrawlerResultRepository;
import com.sportsDetect.crawler.repository.MediaRepository;
import com.sportsDetect.crawler.utils.ImageDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AutomatedService {
    private volatile boolean running = true;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private WebScout webScout;

    @Autowired
    private Validator validator;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PlaywrightCrawlerService playwrightCrawlerService;

    @Autowired
    private RedisQueueService redisQueueService;

    @Autowired
    private LogService logService;

    @Autowired
    private CrawlerResultRepository crawlerResultRepository;


    private final String[] searchEngine = {
            "https://www.google.com/search?q=",
            "https://duckduckgo.com/?q="
    };

    public void startDynamicCrawl(String query) {
        logService.info("[AUTOMATOR] Starting targeted hunt for: " + query);

        String normalizedQuery = query.toLowerCase().trim();

        List<Media> targetMedia = mediaRepository.findAll().stream()
                .filter(m -> m.getTitle() != null &&
                        m.getTitle().toLowerCase().contains(normalizedQuery))
                .collect(Collectors.toList());

        if (targetMedia.isEmpty()) {
            logService.warn("[AUTOMATOR] No matching media found for: " + query);
            return;
        }
        logService.success("[AUTOMATOR] Found " + targetMedia.size() + " matches for: " + query);

        for (Media media : targetMedia) {
            logService.info("[AUTOMATOR] Scouting for: " + media.getTitle());

            for (String engine : searchEngine) {
                String searchUrl = engine + media.getTitle().replace(" ", "+");
                logService.info("[DEBUG] Scouting URL: " + searchUrl);
                Set<String> discoveredLinks = playwrightCrawlerService.extractLinks(searchUrl, media.getTitle());
                logService.info("[DEBUG] Found " + discoveredLinks.size() + " links from scout.");

                for (String link : discoveredLinks) {
                    logService.info("[DEBUG] Attempting to process link: " + link);

                    if (link == null || !link.startsWith("http")) {
                        logService.info("[DEBUG] REJECTED: Not a valid HTTP link");
                        continue;
                    }

                    String lowerLink = link.toLowerCase();
                    boolean isImage = lowerLink.contains(".jpg") ||
                            lowerLink.contains(".png") ||
                            lowerLink.contains(".jpeg");

                    if (!isImage) {
                        logService.info("[DEBUG] REJECTED: Not an image format");
                        continue;
                    }

                    logService.success("[SUCCESS] Filter passed, pushing to Redis: " + link);
                    redisQueueService.push(link + "|" + media.getId());
                    Set<String> finalLinks = new HashSet<>();
                    finalLinks.add(link);

                    if (link.contains("espn") || link.contains("youtube")) {
                        finalLinks.addAll(playwrightCrawlerService.extractLinks(link, media.getTitle()));
                    }

                    for (String finalLink : finalLinks) {
                        if (mediaService.isLinkAlreadyProcessed(finalLink)) continue;
                        redisQueueService.push(finalLink + "|" + media.getId());
                        logService.info("[QUEUE] Added: " + finalLink);
                    }
                }
            }
        }
        logService.info("[AUTOMATOR] Targeted hunt complete for: " + query);

    }
        private void processLink (String link, Media media){
            try {
                if (mediaService.isLinkAlreadyProcessed(link)) {
                    return;
                }

                System.out.println("[TIER 2] Dispatching stealth downloader to: " + link);

                byte[] downloadedImageBytes = ImageDownloader.downloadImage(link);

                if (downloadedImageBytes != null) {
                    System.out.println("[TIER 2] Image secured! Running AI Verification..");

                    mediaService.processDiscovery(link, media, downloadedImageBytes);

                } else {
                    System.out.println("[TIER 2] Blocked or failed: " + link);
                }

            } catch (Exception e) {
                logService.error("[PLAYWRIGHT ERROR] " + e.getMessage());
            }
        }

        @Scheduled(initialDelay = 10000, fixedDelay = 300000)
        public void executeHunt () {
            logService.info("[AUTOMATOR] Starting global piracy hunt...");

            List<Media> protectedMedia = mediaRepository.findAll();
            if (protectedMedia.isEmpty()) {
                logService.warn("[AUTOMATOR] No official media to protect. Going back to sleep.");
                return;
            }

            for (Media media : protectedMedia) {
                logService.info("[AUTOMATOR] Scouting for: " + media.getTitle());

                String title = media.getTitle();
                if(title == null || title.trim().isEmpty()) {
                    logService.warn("[AUTOMATOR] Skipping media with missing title (ID: " + media.getId() + ")");
                    continue;
                }

                for (String engine : searchEngine) {
                    String searchUrl = engine + title.replace(" ", "+");

                    Set<String> discoveredLinks = webScout.scanSiteForLinks(searchUrl, media.getTitle());

                    for (String link : discoveredLinks) {
                        if (link == null || !link.startsWith("http")) continue;

                        if (!(link.endsWith(".jpg") || link.endsWith(".png"))) continue;


                        Set<String> suspiciousLinks = new HashSet<>();
                        suspiciousLinks.add(link);

                        if (isDynamicSite(link)) {

                            suspiciousLinks.addAll(playwrightCrawlerService.extractLinks(link, media.getTitle()));
                        }


                        for (String finalLink : suspiciousLinks) {
                            if (mediaService.isLinkAlreadyProcessed(finalLink)) continue;

                            redisQueueService.push(finalLink + "|" + media.getId());
                            logService.info("[QUEUE] Added: " + finalLink);
                        }
                    }
                }
            }
            logService.info("[AUTOMATOR] Hunt complete.");
        }


        private boolean isDynamicSite (String url){
            return url.contains("espn") || url.contains("youtube");
        }
    }

 */