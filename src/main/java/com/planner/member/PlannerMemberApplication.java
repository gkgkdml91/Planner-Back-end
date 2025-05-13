package com.planner.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@MapperScan(basePackages = {""})
public class PlannerMemberApplication {

	// 주석 추가 테스트 깃 업로드용
	public static void main(String[] args) {
		SpringApplication.run(PlannerMemberApplication.class, args);
	}

}
