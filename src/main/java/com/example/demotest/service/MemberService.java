package com.example.demotest.service;

import com.example.demotest.config.JwtUtil;
import com.example.demotest.entity.Member;
import com.example.demotest.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public Member signupMember(Member member) {
        return memberRepository.save(member);
    }

    public String login(String userId, String password) {
        Member member = memberRepository.findByUserIdAndPassword(userId, password);
        if (member != null) {
            return jwtUtil.generateToken(userId);
        } else {
            return null;
        }
    }
}
