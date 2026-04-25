package com.sportsDetect.crawler.repository;

import com.sportsDetect.crawler.model.UserMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMediaRepository extends JpaRepository<UserMedia, Long> {
    
}
