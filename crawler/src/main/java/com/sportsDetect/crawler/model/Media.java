package com.sportsDetect.crawler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String officialHash;

    private String ownerEmail;
    private LocalDateTime uploadedAt;

    public void setId(Long id) {
        this.id = id;
    }
     public Long getId(){
         return this.id;
     }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOfficialHash() {
        return officialHash;
    }

    public void setOfficialHash(String officialHash) {
        this.officialHash = officialHash;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setFingerprintId(String fingerprintID) {

    }
    /*public Media(String fileName, String hash, String platform) {
        this.fileName = fileName;
        this.hash = hash;
        this.platform = platform;
        this.uploadedAt = LocalDateTime.now();
    }*/

}
