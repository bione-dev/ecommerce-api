package com.bione.api.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.bione.api.ecommerce")
public class EComerceApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(EComerceApiApplication.class, args);
	}
}
