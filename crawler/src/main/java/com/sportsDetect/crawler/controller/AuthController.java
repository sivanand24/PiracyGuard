package com.sportsDetect.crawler.controller;

import com.sportsDetect.crawler.model.AuthResponse;
import com.sportsDetect.crawler.model.LoginRequest;
import com.sportsDetect.crawler.model.User;
import com.sportsDetect.crawler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    //@Autowired
    //private LoginRequest loginRequest;
    //@Autowired
    //private AuthResponse authResponse;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if(userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = UUID.randomUUID().toString();
                return ResponseEntity.ok(new AuthResponse(token, "Login Successful"));
            }
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message","Email already in use"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("account-created-token");
    }


}
