package com.sportsDetect.crawler.engine;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Service;

@Service
public class Validator {

    public byte[] captureEvidence(String url){
        try(Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            try {
                page.navigate(url,new Page.NavigateOptions().setTimeout(30000));
                page.waitForTimeout(5000);

                return page.screenshot(new Page.ScreenshotOptions().setFullPage(false));
            } catch (Exception e) {
                System.out.println("Failed to capture " + url + ": " + e.getMessage());
                return null;
            } finally {
                browser.close();
            }
        }
    }
}
