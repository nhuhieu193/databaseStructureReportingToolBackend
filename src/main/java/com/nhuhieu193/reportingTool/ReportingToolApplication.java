package com.nhuhieu193.reportingTool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ReportingToolApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReportingToolApplication.class, args);
	}
}
