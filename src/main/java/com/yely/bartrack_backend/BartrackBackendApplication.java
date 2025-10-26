package com.yely.bartrack_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BartrackBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BartrackBackendApplication.class, args);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "123456";
		String hash = encoder.encode(rawPassword);
		System.out.println("Hash: " + hash);
		System.out.println("Matches: " + encoder.matches(rawPassword, hash));
	}
}
