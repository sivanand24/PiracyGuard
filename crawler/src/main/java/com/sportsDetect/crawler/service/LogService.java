package com.sportsDetect.crawler.service;



import org.springframework.beans.factory.annotation.Autowired;
import com.sportsDetect.crawler.dto.LogMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class LogService {

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    private static final String DESTINATION = "/topic/logs";

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");


    public void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);

        LogMessage logMessage = new LogMessage(timestamp, level, message);

        System.out.println("[" + timestamp + "] [" + level + "] " + message);

        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend(DESTINATION, logMessage);
        }
    }


    public void info(String message) {
        log("INFO", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void success(String message) {
        log("SUCCESS", message);
    }
}
