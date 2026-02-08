package com.anz.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(scanBasePackages = "com.anz.challenge")
@EnableRetry
public class AnzJavaCodeChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnzJavaCodeChallengeApplication.class, args);
	}

}
