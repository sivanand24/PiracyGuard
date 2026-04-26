package com.sportsDetect.crawler.service;


import com.sportsDetect.crawler.utils.HashUtil;
import com.sportsDetect.crawler.utils.WatermarkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory;


@Service
public class VideoService {
    public class VideoComparisonResult {
        public double matchPercentage;
        public int averageDistance;
    }
    private static final String FRAME_DIR = "frame/";
    private static final String OUTPUT_VIDEO = "output/output.mp4";
    @Autowired
    private HashUtil hashUtil;


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
    public VideoComparisonResult compareVideos(MultipartFile official, MultipartFile suspect) throws Exception {
        Path tempDir = Files.createTempDirectory("video_compare_" + UUID.randomUUID());
        File officialFile = tempDir.resolve("official.mp4").toFile();
        File suspectFile = tempDir.resolve("suspect.mp4").toFile();

        official.transferTo(officialFile);
        suspect.transferTo(suspectFile);

        try {
            List<String> officialHashes = extractAndHash(officialFile, tempDir.resolve("official_frames"));
            List<String> suspectHashes = extractAndHash(suspectFile, tempDir.resolve("suspect_frames"));

            return performFrameComparison(officialHashes, suspectHashes);
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }
    private List<String> extractAndHash(File videoFile, Path frameDir) throws Exception {
        frameDir.toFile().mkdirs();

        ProcessBuilder extract = new ProcessBuilder(
                "ffmpeg", "-i", videoFile.getAbsolutePath(),
                "-vf", "select=not(mod(n\\,120)), scale=320:240",
                "-fps_mode", "vfr",
                frameDir.resolve("frame_%04d.png").toString()
        );
        extract.inheritIO();
        Process p = extract.start();
        p.waitFor();

        List<String> hashes = new ArrayList<>();
        File[] frames = frameDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));

        if (frames != null) {
            for (File frame : frames) {
                byte[] bytes = Files.readAllBytes(frame.toPath());
                hashes.add(HashUtil.generatePHash(bytes));
            }
        }
        return hashes;
    }

    private VideoComparisonResult performFrameComparison(List<String> officialHashes, List<String> suspectHashes) {
        int totalDistance = 0;
        int matches = 0;

        for (String oHash : officialHashes) {
            int bestDist = 64;
            for (String sHash : suspectHashes) {
                int dist = HashUtil.getHammingDistance(oHash, sHash);
                if (dist < bestDist) bestDist = dist;
            }

            if (bestDist < 15) {
                matches++;
            }
            totalDistance += bestDist;
        }

        VideoComparisonResult result = new VideoComparisonResult();
        result.matchPercentage = (officialHashes.isEmpty()) ? 0 : ((double) matches / officialHashes.size()) * 100;
        result.averageDistance = (officialHashes.isEmpty()) ? 64 : (totalDistance / officialHashes.size());

        return result;
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) for (File f : files) deleteDirectory(f);
        dir.delete();
    }
    }
