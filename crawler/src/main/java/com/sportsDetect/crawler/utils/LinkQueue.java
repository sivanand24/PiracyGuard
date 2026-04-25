package com.sportsDetect.crawler.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class LinkQueue {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void add(String link) {
        queue.offer(link);
    }

    public String take() throws InterruptedException {
        return queue.take();
    }
}
