package com.sportsDetect.crawler.repository;

import com.sportsDetect.crawler.model.UserMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sportsDetect.crawler.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<com.sportsDetect.crawler.model.User> findByEmail(String email);
}
