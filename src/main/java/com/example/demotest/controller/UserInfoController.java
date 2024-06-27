package com.example.demotest.controller;

import com.example.demotest.config.JwtUtil;
import com.example.demotest.entity.Member;
import com.example.demotest.service.UserInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/szs")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/scrap")
    public ResponseEntity<Object> scrap(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException {
        // accessToken 조회
        String jwtToken = extractJwtToken(authorizationHeader);
        // accessToken 검증
        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            String userId = jwtUtil.extractUserId(jwtToken);
            return userInfoService.getScrappingData(userId);
        } else {
            return ResponseEntity.status(401).body("Unauthorized Access Token");
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<Object> refund(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // accessToken 조회
        String jwtToken = extractJwtToken(authorizationHeader);
        // accessToken 검증
        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            String userId = jwtUtil.extractUserId(jwtToken);
            return ResponseEntity.ok().body(Map.of("결정세액", userInfoService.getRefundPrice(userId)));
        } else {
            return ResponseEntity.status(401).body("Unauthorized Access Token");
        }
    }

    // Authorization Bearer 토큰 추출
    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}