package com.sportsDetect.crawler.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlaywrightCrawlerService {
    private Playwright playwright;
    private Browser browser;
    private LogService logService;

    @PostConstruct
    public void init() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    public Set<String> extractLinks(String url, String query) {
        Set<String> linksSet = new HashSet<>();
        if (browser == null || !browser.isConnected()) {
            System.err.println("Browser not connected!");
            return linksSet;
        }

        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .setViewportSize(1280, 800);

        try (BrowserContext context = browser.newContext(options)) {
            Page page = context.newPage();

            page.navigate("https://duckduckgo.com/?q=" + query);
            page.querySelectorAll("img");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
            page.waitForTimeout(2000);

            List<ElementHandle> images = page.querySelectorAll("img");
            for (ElementHandle img : images) {
                String src = img.getAttribute("src");
                if (src != null) {
                    linksSet.add(src);
                }
            }

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
