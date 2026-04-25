package com.sportsDetect.crawler.dto;

import lombok.Data;

@Data
public class LogMessage {
    private String time;
    private String level;
    private String message;

    public LogMessage(String time, String level, String message) {
        this.time = time;
        this.level = level;
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}
