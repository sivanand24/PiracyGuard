package com.sportsDetect.crawler.utils;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class HashUtil {
    public static String generatePHash(String imageData){
        try{
            File file = new File(imageData);
            BufferedImage img = ImageIO.read(file);
            if(img == null){
                System.out.println("Could not find or read the image at: " + imageData);
                return null;
            }
            return computeHash(img);
        } catch (Exception e){
            System.err.println("Hash Generation Error: " + e.getMessage());
            return null;
        }
    }
    public static String generatePHash(byte[] imageBytes) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(imageBytes);
            BufferedImage img = ImageIO.read(bais);

            if (img == null) {
                System.out.println("Could not read the image bytes from the upload.");
                return null;
            }
            return computeHash(img);
        } catch (Exception e) {
            System.err.println("upload Hash Generation Error: " + e.getMessage());
            return null;
        }
    }
        private static String computeHash(BufferedImage img){
            BufferedImage resized = new BufferedImage(9, 8, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = resized.createGraphics();
            g.drawImage(img, 0, 0, 9, 8, null);
            g.dispose();

            StringBuilder hash = new StringBuilder();
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    int left = resized.getRGB(x, y) & 0xFF;
                    int right = resized.getRGB(x + 1, y) & 0xFF;
                    hash.append(left > right ? "1" : "0");
                }
            }

            return binaryToHex(hash.toString());
    }

    private static String binaryToHex(String binary) {
        long l = Long.parseUnsignedLong(binary, 2);
        return String.format("%016x", l);
    }

    public static int getHammingDistance(String hash1, String hash2) {
        long h1 = Long.parseUnsignedLong(hash1, 16);
        long h2 = Long.parseUnsignedLong(hash2, 16);
        return Long.bitCount(h1 ^ h2);
    }

    public String getRiskLevel(int distance) {
        if (distance <= 10) {
            return "CRITICAL (Exact Match)";
        } else if (distance <= 25) {
            return "HIGH (Likely pirated/Modified";
        } else if (distance <= 40) {
            return "MEDIUM (Suspicious Overlay/Crop";
        } else {
            return "LOW (Unrelated Content)";
        }
    }
}
