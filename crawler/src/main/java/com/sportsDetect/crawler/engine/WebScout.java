package com.sportsDetect.crawler.engine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sportsDetect.crawler.service.LogService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class WebScout {
    private static final int TIMEOUT = 10000;
    @Autowired
    private LogService logService;


    public Set<String> scanSiteForLinks(String seedUrl, String mediaTitle) {

        Set<String> discoveredLinks = new HashSet<>();

        try {

            if (seedUrl == null || mediaTitle == null) {
                return discoveredLinks;
            }

            Connection.Response response = Jsoup.connect(seedUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(15000)
                    .ignoreContentType(true)
                    .execute();

            String contentType = response.contentType();

            if (contentType == null || !contentType.contains("text/html")) {
                logService.warn("[CRAWLER] Skipping non-HTML: " + seedUrl);
                return discoveredLinks;
            }

            Document doc = response.parse();

            Elements images = doc.select("img[src]");

            for (Element img : images) {
                String src = img.attr("abs:src");

                if (src == null || src.isEmpty()) continue;

                if (src.length() > 50 &&
                        !src.contains("logo") &&
                        !src.contains("icon") &&
                        !src.contains("sprite")) {

                    discoveredLinks.add(src);
                }
            }

        } catch (Exception e) {
            logService.error("[CRAWLER ERROR] " + seedUrl + " → " + e.getMessage());
        }

        return discoveredLinks;
    }

    private String normalizeUrl(String url) {
        return url.split("#")[0];
    }

    private String getRandomUserAgent() {
        String[] agents = {
                "Mozilla/5.0",
                "Chrome/120.0",
                "Safari/537.36"
        };
        return agents[new Random().nextInt(agents.length)];
    }

}
