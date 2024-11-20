package com.olegandreevich.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TmsApplication {

	public static void main(String[] args) {

		SpringApplication.run(TmsApplication.class, args);
		String plainTextPassword = "admin"; // Ваш пароль

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(plainTextPassword);

		System.out.println("===============================================Хэшированный пароль: " + encodedPassword);
	}

}
