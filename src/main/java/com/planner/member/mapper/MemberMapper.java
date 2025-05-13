package com.planner.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.planner.member.model.Member;

@Mapper
public interface MemberMapper {
    Member findById(int id);
    Member findByAll(@Param("name") String name, @Param("email") String email, @Param("phone") String phone);
    void insertMember(Member member);
    Member findByEmailAndPassword(@Param("email") String email, @Param("pw") String pw);
    Member findByEmail(@Param("email") String email);
    void updateMember(Member member);
    String selectEmailByNameAndPhone(@Param("name") String name, @Param("phone") String phone);
    int countByNameEmailPhone(@Param("name") String name, @Param("email") String email, @Param("phone") String phone);
    void updatePassword(@Param("id") int id, @Param("pw") String pw);
}
