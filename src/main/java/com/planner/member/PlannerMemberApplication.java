package com.planner.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@MapperScan(basePackages = {""})
public class PlannerMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlannerMemberApplication.class, args);
	}

}
