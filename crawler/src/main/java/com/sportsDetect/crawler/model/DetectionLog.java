package com.sportsDetect.crawler.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class DetectionLog {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String sourceUrl;
    private String platform;
    private int hammingDistance;
    private String riskLevel;
    private String confidenceScore;

    @Column(length = 5000)
    private String foundHash;
    private LocalDateTime detectedAt;

    @ManyToOne
    @JoinColumn(name = "media_id")
    private Media originalMedia;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public int getHammingDistance() {
        return hammingDistance;
    }

    public void setHammingDistance(int hammingDistance) {
        this.hammingDistance = hammingDistance;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getFoundHash() {
        return foundHash;
    }

    public void setFoundHash(String foundHash) {
        this.foundHash = foundHash;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public Media getOriginalMedia() {
        return originalMedia;
    }

    public void setOriginalMedia(Media originalMedia) {
        this.originalMedia = originalMedia;
    }

}
