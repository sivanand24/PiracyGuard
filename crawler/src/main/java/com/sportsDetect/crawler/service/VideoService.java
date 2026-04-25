package com.sportsDetect.crawler.service;


import com.sportsDetect.crawler.utils.HashUtil;
import com.sportsDetect.crawler.utils.WatermarkUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VideoService {
    private static final String FRAME_DIR = "frame/";
    private static final String OUTPUT_VIDEO = "output/output.mp4";


    public List<String> processVideo(String inputPath, String fingerprintID) throws Exception {

        new File(FRAME_DIR).mkdirs();
        new File("output/").mkdirs();

        File folder = new File(FRAME_DIR);
        File[] files = folder.listFiles();

        ProcessBuilder extract = new ProcessBuilder(
                "ffmpeg",
                "-i", inputPath,
                "-vf", "select=not(mod(n\\,120)), scale= 320:240",
                "-fps_mode", "vfr",
                FRAME_DIR + "frame_%04d.png"
        );
        extract.inheritIO();
        extract.start().waitFor();

        if (!folder.exists()) {
            folder.mkdirs();
        }
        List<String> hashes = new ArrayList<>();
        if (files != null) {

            Arrays.stream(files).parallel().forEach(file -> {
                try {
                    if (!file.getName().endsWith(".png")) {
                        return;
                    }
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    byte[] watermarked = WatermarkUtil.applyInvisibleWatermark(bytes, fingerprintID);

                    Files.write(file.toPath(), watermarked);
                    String hash = HashUtil.generatePHash(watermarked);
                    synchronized (hashes) {
                        hashes.add(hash);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
       /* ProcessBuilder rebuild = new ProcessBuilder(
                "ffmpeg",
                "-framerate", "30",
                "-i", FRAME_DIR + "frame_%04d.png",
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                OUTPUT_VIDEO
        );
        rebuild.inheritIO();
        rebuild.start().waitFor(); */

        for (File file : files) {
            file.delete();
        }
        Process extractProcess = extract.start();
        try {
            extractProcess.waitFor();
        } catch (InterruptedException e) {
            System.out.println("Process interrupted during shutdown");
            Thread.currentThread().interrupt();
        }
        return hashes;
    }
    }
