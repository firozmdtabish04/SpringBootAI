package com.tabish.proproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tabish.proproject.entity.Role;
import com.tabish.proproject.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ProProjectApplication {

	public static void main(String[] args) {
		loadEnvFile();
		SpringApplication.run(ProProjectApplication.class, args);
	}

	private static void loadEnvFile() {
		java.io.File envFile = new java.io.File(".env");
		if (envFile.exists() && envFile.isFile()) {
			try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
						int idx = line.indexOf('=');
						String key = line.substring(0, idx).trim();
						String value = line.substring(idx + 1).trim();
						if (System.getProperty(key) == null && System.getenv(key) == null) {
							System.setProperty(key, value);
						}
					}
				}
			} catch (Exception ignored) {
			}
		}
	}

	@Bean
	public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {

		return args -> {
			String email = "mdtabishfiroz4@gmail.com";

			userRepository.findByEmail(email).ifPresentOrElse(user -> {
				user.setRole(Role.ROLE_ADMIN);
				user.setEnabled(true);

				userRepository.save(user);

				log.info("Administrator role assigned to account: {}", email);

			}, () -> log.warn("User with email {} was not found. No account was created.", email));
		};
	}
}