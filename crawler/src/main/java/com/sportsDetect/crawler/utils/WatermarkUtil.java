package com.sportsDetect.crawler.utils;

import com.sun.jdi.event.ExceptionEvent;
import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.UUID;

@Component
public class WatermarkUtil {
    public static String generateFingerprintID(String ownerEmail) {
        try {
            String rawData = ownerEmail + UUID.randomUUID().toString() + System.currentTimeMillis();
            MessageDigest digest = MessageDigest.getInstance("SHA_256");
            byte[] hash = digest.digest(rawData.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return "ID-" + hexString.toString().substring(0, 16).toUpperCase();

        } catch (Exception e) {
            return "ID-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        }
    }

    public static byte[] applyInvisibleWatermark(byte[] originalImageByte, String fingerprintID) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalImageByte));
            String message = fingerprintID + "[END]";
            StringBuilder binaryMessage = new StringBuilder();
            for (char c : message.toCharArray()) {
                binaryMessage.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));

            }
            int messageLength = binaryMessage.length();
            int bitIndex = 0;

            outerLoop:
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    if (bitIndex < messageLength) {
                        int pixel = img.getRGB(x, y);
                        int bitToHide = binaryMessage.charAt(bitIndex) - '0';

                        pixel = (pixel & 0xFFFFFFFF) | bitToHide;
                        img.setRGB(x, y, pixel);
                        bitIndex++;
                    } else {
                        break outerLoop;
                    }
                }
            }

            ByteArrayOutputStream bios = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bios);
            return bios.toByteArray();

        } catch (Exception e) {
            System.err.println("Watermark Failed: " + e.getMessage());
            return originalImageByte;
        }
    }
}