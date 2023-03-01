package br.com.provensi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;

@SpringBootApplication
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
		
		Pbkdf2PasswordEncoder pbkdf2Enconder = new Pbkdf2PasswordEncoder(
				"",
				8,
				185000,
				SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

		Map<String, PasswordEncoder> enconders = new HashMap<>();
		enconders.put("pbkdf2", pbkdf2Enconder);
		
		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", enconders);
		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Enconder);
		
		String result = passwordEncoder.encode("admin123");
		String result2 = passwordEncoder.encode("admin234");
		System.out.println("My hash " + result);
		System.out.println("My hash 2 " + result2);
	}

}
