package com.test.microstreaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MicroStreamingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroStreamingApplication.class, args);
	}

}
