package com.sportsDetect.crawler.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class CrawlResult {
    @Id
    @GeneratedValue
    private Long id;

    private String query;
    private String link;
    private String status;

    public CrawlResult() {}

    public CrawlResult(String query, String link, String status) {
        this.query = query;
        this.link = link;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
