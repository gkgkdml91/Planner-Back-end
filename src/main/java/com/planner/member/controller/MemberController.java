package com.planner.member.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.planner.member.model.Member;
import com.planner.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/member")
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MemberController {

	@Value("${file.path}")
	private String filePath;
	
    private final MemberService memberService;

    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
    }
    
    @GetMapping("/{id}")
    public Member getMember(@PathVariable int id) {
        return memberService.getMemberById(id);
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody Member member) {
        try {
            memberService.insertMember(member);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("회원가입 실패: " + e.getMessage());
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<String> modifyMember(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("pw") String pw,
            @RequestParam(value = "profile", required = false) MultipartFile profileFile,
            @RequestParam(value = "existingProfile", required = false) String existingProfile
    ) {
        try {
            Member member = new Member();
            member.setName(name);
            member.setEmail(email);
            member.setPhone(phone);
            member.setPw(pw);
            member.setUpdated_at(LocalDateTime.now());

            if (profileFile != null && !profileFile.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                String originalFileName = profileFile.getOriginalFilename();
                String fileName = uuid + "_" + originalFileName;

                String fullUploadPath = filePath + "/uploads/profile";
                File uploadDir = new File(fullUploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                File dest = new File(uploadDir, fileName);
                profileFile.transferTo(dest);

                String dbPath = "/uploads/profile/" + fileName;
                member.setProfile(dbPath);
            } else {
                // 📌 새 파일 없으면 기존 경로 유지
                member.setProfile(existingProfile);
            }

            memberService.updateMember(member);
            return ResponseEntity.ok("회원정보 수정 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("회원정보 수정 실패: " + e.getMessage());
        }
    }
    
    @GetMapping("/cookiechk")
    public ResponseEntity<?> cookieCheck(HttpServletRequest request) {
    	// 기존 세션만 가져옴, 없으면 null
        HttpSession session = request.getSession(false); 
        if (session != null) {
            Member loginMember = (Member) session.getAttribute("loginMember");
            if (loginMember != null) return ResponseEntity.ok(loginMember);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member, HttpSession session) {
        try {
            Member loginMember = memberService.login(member.getEmail(), member.getPw());

            if (loginMember != null) {
                session.setAttribute("loginMember", loginMember);
                session.setMaxInactiveInterval(60 * 60);
                return ResponseEntity.ok(loginMember);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 잘못된 정보");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 서버 오류");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/mypage")
    public ResponseEntity<Member> getMyPage(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // DB에서 최신 회원 정보 조회
        Member member = memberService.getMemberById(loginMember.getId());

        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(member);
    }

    @PostMapping("/findId")
    public ResponseEntity<String> findId(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String phone = body.get("phone");

        String email = memberService.findEmailByNameAndPhone(name, phone);
        if (email != null) {
            return ResponseEntity.ok(email + " 입니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 계정이 없습니다.");
        }
    }
    
    @PostMapping("/findPw")
    @ResponseBody
    public ResponseEntity<String> findPassword(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String phone = request.get("phone");

        if (!memberService.checkUserForPasswordReset(name, email, phone)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원 정보가 일치하지 않습니다.");
        }

        String newPassword = memberService.resetPassword(name, email, phone);
        if (newPassword != null) {
            return ResponseEntity.ok(newPassword);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 재설정 실패");
        }
    }

}