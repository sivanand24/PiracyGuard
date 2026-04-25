package com.sportsDetect.crawler.repository;

import com.sportsDetect.crawler.model.CrawlResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlerResultRepository extends JpaRepository<CrawlResult, Long> {

}
