package com.jose.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TicketApplication {
	    public static void main(String[] args) {
	        SpringApplication.run(TicketApplication.class, args);
	    }

}
