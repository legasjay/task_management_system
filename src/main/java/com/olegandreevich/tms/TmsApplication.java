package com.olegandreevich.tms;

import com.olegandreevich.tms.servicies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TmsApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {

		SpringApplication.run(TmsApplication.class, args);
		String plainTextPassword = "admin"; // Ваш пароль


	}

	@Override
	public void run(String... args) throws Exception {
		try {
			String plainTextPassword = "admin"; // Ваш новый пароль
			userService.updatePassword("admin", plainTextPassword);
			System.out.println("Пароль успешно обновлён!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
