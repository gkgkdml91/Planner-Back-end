package com.planner.member.controller;

import com.planner.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dbtest")
public class DBTestController {

    private final MemberService memberService;

    public DBTestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<String> testDbConnection() {
        try {
            memberService.testQuery();
            return ResponseEntity.ok("✅ DB 연결 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ DB 연결 실패!");
        }
    }
}
