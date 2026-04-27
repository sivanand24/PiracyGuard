package com.sportsDetect.crawler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LandingController {
    @GetMapping("/")
    public String index() {
        return "PiracyGuard API is live. Use the /api endpoints to interact with the service.";
    }
}
