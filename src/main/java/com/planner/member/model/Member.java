package com.planner.member.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;
    
    // 읽기만 가능하도록 하는 어노테이션
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pw;
    
    private String profile;
    private LocalDateTime created_at; 
    private LocalDateTime updated_at;

    // 기본 생성자
    public Member() {}

    // 전체 필드 생성자
    public Member(int id, String name, String email, String phone, String pw, String profile, LocalDateTime updated_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pw = pw;
        this.profile = profile;
        this.updated_at = updated_at;
    }

    // Getter Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPw() { return pw; }
    public void setPw(String pw) { this.pw = pw; }

    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
    
    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public LocalDateTime getUpdated_at() { return updated_at; }
    public void setUpdated_at(LocalDateTime updated_at) { this.updated_at = updated_at; }
}
