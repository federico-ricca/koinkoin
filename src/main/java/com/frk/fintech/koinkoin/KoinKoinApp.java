package com.frk.fintech.koinkoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("xyz.grafema")
public class KoinKoinApp extends SpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(KoinKoinApp.class, args);
	}
}
