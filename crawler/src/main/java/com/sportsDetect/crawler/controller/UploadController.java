package com.sportsDetect.crawler.controller;

import com.sportsDetect.crawler.model.Media;
import com.sportsDetect.crawler.model.UserMedia;
import com.sportsDetect.crawler.repository.MediaRepository;
import com.sportsDetect.crawler.repository.UserMediaRepository;
import com.sportsDetect.crawler.service.VideoService;
import com.sportsDetect.crawler.utils.HashUtil;
import com.sportsDetect.crawler.utils.WatermarkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private VideoService videoService;
    @Autowired
    private WatermarkUtil watermarkUtil;
    @Autowired
    private UserMediaRepository userMediaRepository;



    public String handleOfficialUpload(@RequestBody byte[] uploadBytes, @RequestParam String ownerEmail) {
        String fingerprintID = WatermarkUtil.generateFingerprintID(ownerEmail);
        System.out.println("Generated Fingerprint: " + fingerprintID);

        byte[] watermarkedBytes = WatermarkUtil.applyInvisibleWatermark(uploadBytes, fingerprintID);

        Media officialMedia = new Media();
        officialMedia.setOwnerEmail(ownerEmail);
        officialMedia.setFingerprintId(fingerprintID);
        officialMedia.setOfficialHash(HashUtil.generatePHash(watermarkedBytes));
        mediaRepository.save(officialMedia);
        return "Uploaded successfully";
    }

    @PostMapping("/files")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("ownerEmail") String ownerEmail){
        try {
            String fingerprintID = watermarkUtil.generateFingerprintID(ownerEmail);
            Path tempPath = Files.createTempFile("upload_", file.getOriginalFilename());
            Files.write(tempPath, file.getBytes());

            List<String> hashList ;

            String combinedHash;

            if(file.getContentType().startsWith("video")){
                hashList = videoService.processVideo(tempPath.toString(),fingerprintID);
                combinedHash = String.join(",", hashList);
            }
            else {
                byte[] watermarked = WatermarkUtil.applyInvisibleWatermark(file.getBytes(), fingerprintID);

                Path outputDirectory = Path.of("output");
                String filename = "image_" + System.currentTimeMillis() + ".png";
                Path imagePath = outputDirectory.resolve(filename);

                if (!Files.exists(outputDirectory)) {
                    Files.createDirectories(outputDirectory);
                    System.out.println("DEBUG: Created 'output' directory on server.");
                }

                Files.write(imagePath, watermarked);

                combinedHash = HashUtil.generatePHash(watermarked);
            }

            Media media = new Media();
            media.setOwnerEmail(ownerEmail);
            media.setFingerprintId(fingerprintID);
            media.setOfficialHash(combinedHash);

            mediaRepository.save(media);
            return " Upload + Watermark SUCCESS";
        } catch (Exception e){
            e.printStackTrace();
            return " Failed at watermark stage: " + e.getMessage();
        }
    }
    @PostMapping("/verify-video")
    public String verifyVideo(@RequestParam("file") MultipartFile file){
        try {
            byte[] bytes = file.getBytes();
            String hash = HashUtil.generatePHash(bytes);
            var result = mediaRepository.findByOfficialHash(hash);

            if (!result.isEmpty()) {
                return " Copyright detected! Owner: " + result.get(0).getOwnerEmail();
            } else {
                return " Original video";
            }
        }
        catch (Exception e) {
                return "Error: " + e.getMessage();
            }
    }
    @GetMapping("/all")
    public List<UserMedia> getAllMedia(){
        return userMediaRepository.findAll();
    }
}
