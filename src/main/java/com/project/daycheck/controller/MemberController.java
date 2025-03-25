package com.project.daycheck.controller;

import com.project.daycheck.entity.Member;
import com.project.daycheck.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 현재 인증된 사용자 정보 조회 API
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentMember(@AuthenticationPrincipal UserDetails userDetails){
        Member member = memberService.findMemberByEmail(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("id", member.getId());
        response.put("email", member.getEmail());
        response.put("name", member.getName());
        response.put("role", member.getRole());

        return ResponseEntity.ok(response);

    }
}
