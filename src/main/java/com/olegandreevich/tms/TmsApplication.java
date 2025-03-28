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
		String plainTextPassword = "admin";


	}

	@Override
	public void run(String... args) throws Exception {
		try {
			String plainTextPassword = "admin";
			userService.updatePassword("admin", plainTextPassword);
			userService.updatePassword("user1", plainTextPassword);
			userService.updatePassword("user2", plainTextPassword);
			System.out.println("Пароль успешно обновлён!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
