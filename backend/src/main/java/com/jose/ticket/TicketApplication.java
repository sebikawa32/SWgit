package com.jose.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TicketApplication {

	public static void main(String[] args) {
		// JVM 기본 타임존을 UTC로 설정
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(TicketApplication.class, args);
	}
}
