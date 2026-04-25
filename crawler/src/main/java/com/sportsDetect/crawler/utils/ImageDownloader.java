package com.sportsDetect.crawler.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ImageDownloader {
    public static byte[] downloadImage(String imageUrl){
        try{
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("User.java-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            if(connection.getResponseCode() == 200){
                InputStream in = connection.getInputStream();
                return in.readAllBytes();
            } else {
                System.out.println("[DOWNLOAD FAILED] HTTP Error: " + connection.getResponseCode());
                return null;
            }
        } catch (Exception e){
            System.err.println("[DOWNLOAD ERROR] " + e.getMessage());
            return null;
        }
    }
}
