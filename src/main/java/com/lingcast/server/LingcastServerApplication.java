package com.lingcast.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LingcastServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LingcastServerApplication.class, args);
	}

}
