package com.sportsDetect.crawler.repository;

import com.sportsDetect.crawler.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByOfficialHash(String officialHash);
}


