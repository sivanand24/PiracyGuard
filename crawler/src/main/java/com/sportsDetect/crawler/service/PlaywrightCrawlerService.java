package com.sportsDetect.crawler.service;

/*import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PlaywrightCrawlerService {
    private Playwright playwright;
    private Browser browser;

    @Autowired
    private LogService logService;

    @PostConstruct
    public void init() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }
    public synchronized Set<String> extractLinks(String url, String query) {
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                .setExtraHTTPHeaders(Map.of("Accept-Language", "en-US,en;q=0.9"));
        Set<String> linksSet = new HashSet<>();
        if (browser == null || !browser.isConnected()) init();

        try (BrowserContext context = browser.newContext(options)) {
            Page page = context.newPage();

            page.navigate(url);

            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
            page.waitForTimeout(3000);

            // FETCH ALL IMAGES
            List<ElementHandle> images = page.querySelectorAll("img");
            System.out.println("DEBUG: Scraped " + images.size() + " total images from page.");

            for (ElementHandle img : images) {
                String src = img.getAttribute("src");
                String dataSrc = img.getAttribute("data-src");

                // Prioritize data-src (often the high-res version)
                String finalUrl = (dataSrc != null && !dataSrc.isEmpty()) ? dataSrc : src;

                if (finalUrl != null && finalUrl.startsWith("http")) {
                    linksSet.add(finalUrl);
                }
            }
            page.close();
        } catch (Exception e) {
            logService.error("[PLAYWRIGHT ERROR] " + e.getMessage());
        }
        return linksSet;
    }
    @PreDestroy
    public void shutdown() {
        browser.close();
        playwright.close();
    }
}

 */
