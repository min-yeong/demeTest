package com.example.demotest.repository;

import com.example.demotest.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUserIdAndPassword(String userId, String password);

    Member findMemberByUserId(String userId);

}