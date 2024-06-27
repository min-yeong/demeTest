package com.example.demotest.controller;

import com.example.demotest.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demotest.service.MemberService;

import java.util.Map;

@RestController
@RequestMapping("/szs")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Member> signup(@RequestBody Member member) {
        Member createdMember = memberService.signupMember(member);
        return ResponseEntity.ok(createdMember);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Member member) {
        String token = memberService.login(member.getUserId(), member.getPassword());
        if (token != null) {
            // accessToken Response
            return ResponseEntity.ok().body(Map.of("accessToken", token));
        } else {
            return ResponseEntity.badRequest().body("Login failed. Invalid userId or password.");
        }
    }

}
