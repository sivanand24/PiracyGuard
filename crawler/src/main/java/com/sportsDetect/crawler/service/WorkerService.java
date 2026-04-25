package com.sportsDetect.crawler.service;

import com.sportsDetect.crawler.model.Media;
import com.sportsDetect.crawler.repository.CrawlerResultRepository;
import com.sportsDetect.crawler.repository.MediaRepository;
import com.sportsDetect.crawler.utils.ImageDownloader;
import com.sportsDetect.crawler.utils.LinkQueue;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkerService {

    @Autowired
    private LinkQueue linkQueue;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private LogService logService;

    @Autowired
    private RedisQueueService redisQueueService;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private CrawlerResultRepository crawlerResultRepository;

    private volatile boolean running = true;
    private Thread workerThread;

    @PostConstruct
    public void startWorkers() {
        workerThread = new Thread(this::processLoop,"worker-thread");
        workerThread.start();
    }

    private void processLoop(){
        while(running){
            try {
                String item = redisQueueService.blockingPop();

                if (item == null) {
                    Thread.sleep(1000);
                    continue;
                }
                String[] parts = item.split("\\|");
                String link = parts[0];
                Long mediaId = Long.parseLong(parts[0]);

                Media media = mediaRepository.findById(mediaId).orElse(null);

                if (media == null) {
                    logService.warn("[WORKER] Media not found for ID: " + mediaId);
                    continue;
                }


                logService.info("[WORKER] Processing: " + link);

                byte[] data = ImageDownloader.downloadImage(link);

                if (data != null) {
                    logService.success("[WORKER] Downloaded: " + link);
                    mediaService.processDiscovery(link, media, data);

                } else {
                    logService.warn("[WORKER] Failed: " + link);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logService.error("[WORKER] Interrupted");
                break;
            } catch (Exception e){
                logService.error("[ERROR] " + e.getMessage());
            }
        }
    }
    @PreDestroy
    public void stop(){
        running = false;
        if(workerThread != null){
            workerThread.interrupt();
        }
    }

    private void sleepQuiet(long ms){
        try{ Thread.sleep(ms);}
        catch (InterruptedException ignored){
            Thread.currentThread().interrupt();
        }
    }
    private byte[] downloadWithRetry(String link) {
        int attempts = 3;

        for (int i = 0; i < attempts; i++) {
            byte[] data = ImageDownloader.downloadImage(link);
            if (data != null) return data;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
        }

        return null;
    }
}
