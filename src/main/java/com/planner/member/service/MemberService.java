package com.planner.member.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.planner.member.mapper.MemberMapper;
import com.planner.member.model.Member;

@Service
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Member getMemberById(int id) {
        return memberMapper.findById(id);
    }

    public void insertMember(Member member) {
        member.setPw(passwordEncoder.encode(member.getPw()));
        memberMapper.insertMember(member);
    }

    public Member login(String email, String pw) {
        Member member = memberMapper.findByEmail(email);
        if (member != null && passwordEncoder.matches(pw, member.getPw())) {
            return member;
        }
        return null;
    }

    public Member findByEmail(String email) {
        return memberMapper.findByEmail(email);
    }

    public void updateMember(Member member) {
        Member existing = memberMapper.findByEmail(member.getEmail());
        if (member.getPw() != null && !member.getPw().isEmpty()
            && !passwordEncoder.matches(member.getPw(), existing.getPw())) {
            // 새 비밀번호가 기존과 다르면 암호화
            member.setPw(passwordEncoder.encode(member.getPw()));
        } else {
            // 기존 비밀번호 그대로 유지
            member.setPw(existing.getPw());
        }
        memberMapper.updateMember(member);
    }


    public String findEmailByNameAndPhone(String name, String phone) {
        return memberMapper.selectEmailByNameAndPhone(name, phone);
    }

    public boolean checkUserForPasswordReset(String name, String email, String phone) {
        return memberMapper.countByNameEmailPhone(name, email, phone) > 0;
    }

    public String resetPassword(String name, String email, String phone) {
        Member member = memberMapper.findByAll(name, email, phone);
        if (member != null) {
            String newPw = UUID.randomUUID().toString().substring(0, 8); // 임시 비밀번호
            String encoded = passwordEncoder.encode(newPw);
            memberMapper.updatePassword(member.getId(), encoded); // ID 기준으로 수정
            return newPw;
        }
        return null;
    }

    public void testQuery() {
        memberMapper.findById(1);
    }
}
